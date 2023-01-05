package com.lzb.rock.mqtt.context;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.bson.types.ObjectId;

import com.lzb.rock.base.holder.SpringContextHolder;
import com.lzb.rock.base.model.MyDelayed;
import com.lzb.rock.mqtt.mapper.SendMsgMapper;
import com.lzb.rock.mqtt.mapper.SequenceMapper;
import com.lzb.rock.mqtt.model.SendMsg;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.AttributeKey;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户ChannelHandlerContext对象
 *
 * @author lzb
 *
 * @date 2019年9月30日 下午11:13:36
 */
@Slf4j
public class MyNettyContext {

	public static final AttributeKey<String> CLIENT_ID_KEY = AttributeKey.valueOf("CLIENT_ID_KEY");

	public static final AttributeKey<Map<String, Integer>> TOPIC_KEY = AttributeKey.valueOf("TOPIC_KEY");

	public static final AttributeKey<Boolean> CLEAN_SESSION_KEY = AttributeKey.valueOf("CLEAN_SESSION_KEY");

	public static Long delayedTime = 10000L;

	/**
	 * 在线用户，账号为key
	 */
	private final static ConcurrentMap<String, Channel> channelAll = PlatformDependent.newConcurrentHashMap();

	private final static ConcurrentMap<String, Set<String>> topicGroup = PlatformDependent.newConcurrentHashMap();

	private final static Object topicGroupLock = new Object();

	public static void putAll(String clientId, Channel channel) {
		channelAll.put(clientId, channel);
	}

	public static void disConnect(Channel channel) {
		String clientId = channel.attr(MyNettyContext.CLIENT_ID_KEY).get();
		Boolean cleanSession = channel.attr(MyNettyContext.CLEAN_SESSION_KEY).get();
		channelAll.remove(clientId);
		if (cleanSession == null || cleanSession) {
			Map<String, Integer> map = channel.attr(MyNettyContext.TOPIC_KEY).get();
			if (map != null) {
				for (Entry<String, Integer> entry : map.entrySet()) {
					try {
						topicGroup.get(entry.getKey()).remove(clientId);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}

		}
	}

	public static void subscribe(String topic, String clientId) {
		Set<String> channelGroup = topicGroup.get(topic);

		if (channelGroup == null) {
			synchronized (topicGroupLock) {
				channelGroup = topicGroup.get(topic);
				if (channelGroup == null) {
					channelGroup = new CopyOnWriteArraySet<String>();
					topicGroup.put(topic, channelGroup);
				}
			}
		}
		channelGroup.add(clientId);
	}

	public static Channel getChannel(String clientId) {
		return channelAll.get(clientId);
	}

	public static void send(String topic, byte[] msg, ObjectId oldId) {
		Set<String> set = topicGroup.get(topic);
		for (String clientId : set) {
			send(clientId, topic, msg, oldId, null);
		}
	}

	public static void send(String clientId, String topic, byte[] payload, ObjectId oldId, Integer packetId) {

		SequenceMapper sequenceMapper = SpringContextHolder.getBean(SequenceMapper.class);
		if (packetId == null) {
			Long nextId = sequenceMapper.nextId(clientId, "packetId");
			packetId = (int) (nextId % 65535);
		}

		SendMsg sendMsg = new SendMsg();
		ObjectId _id = new ObjectId();
		sendMsg.set_id(_id);
		sendMsg.setOldId(oldId);
		sendMsg.setClientId(clientId);
		sendMsg.setAck(MqttMessageType.PUBLISH.value());
		sendMsg.setPayload(new String(payload));
		sendMsg.setPacketId(packetId);
		sendMsg.setCount(0);
		sendMsg.setTopic(topic);
		sendMsg.setAckCount(0);
		Channel channel = channelAll.get(clientId);
		SendMsgMapper sendMsgMapper = SpringContextHolder.getBean(SendMsgMapper.class);

		if (channel == null) {
			// 记录离线消息
			sendMsg.setAck(-1);
			sendMsgMapper.insert(sendMsg);
			return;
		}

		Integer qos = channel.attr(MyNettyContext.TOPIC_KEY).get().get(topic);
		sendMsg.setQos(qos);
		if (1 == qos) {
			// 延时检测，未收到ack 重试
			MyDelayed<ObjectId> myDelayed = new MyDelayed<ObjectId>(_id, delayedTime);
			DelayContext.offer(myDelayed);
		}

		// 记录发送消息
		sendMsgMapper.insert(sendMsg);

		send(channel, topic, qos, payload, packetId);

	}

	/**
	 * 发离线消息
	 * 
	 * @param _id
	 */
	public static void send(SendMsg sendMsg) {

		SendMsgMapper sendMsgMapper = SpringContextHolder.getBean(SendMsgMapper.class);

		Channel channel = channelAll.get(sendMsg.getClientId());
		if (channel == null) {
			return;
		}

		Integer qos = channel.attr(MyNettyContext.TOPIC_KEY).get().get(sendMsg.getTopic());

		sendMsg.setQos(qos);

		if (1 == qos) {
			// 延时检测，未收到ack 重试
			MyDelayed<ObjectId> myDelayed = new MyDelayed<ObjectId>(sendMsg.get_id(), delayedTime);
			DelayContext.offer(myDelayed);
		}
		sendMsgMapper.updateMultiAck(sendMsg.getClientId(), sendMsg.getPacketId(), -1, MqttMessageType.PUBLISH.value());
		send(channel, sendMsg.getTopic(), qos, sendMsg.getPayload().getBytes(), sendMsg.getPacketId());

	}

	public static void send(Channel channel, String topic, Integer qos, byte[] payload, Integer packetId) {
		if (channel == null) {
			log.error("channel 为空;topic:{};qos:{};payload:{};packetId:{}", topic, qos, new String(payload), packetId);
			return;
		}
		if (qos == null) {
			qos = 0;
		}
		String clientId = channel.attr(MyNettyContext.CLIENT_ID_KEY).get();
		log.info("发送消息;clientId:{};topic:{};qos:{};payload:{};packetId:{}", clientId, topic, qos, new String(payload),
				packetId);
		MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.valueOf(qos),
				false, payload.length);
		MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(topic, packetId);
		MqttPublishMessage message2 = new MqttPublishMessage(mqttFixedHeader, variableHeader,
				Unpooled.buffer().writeBytes(payload));
		channel.writeAndFlush(message2);
	}

}
