package com.lzb.rock.mqtt.service.impl;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lzb.rock.base.util.UtilString;
import com.lzb.rock.mqtt.context.MyNettyContext;
import com.lzb.rock.mqtt.mapper.PubMsgMapper;
import com.lzb.rock.mqtt.model.PubMsg;
import com.lzb.rock.mqtt.service.IMqttReadService;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;

/**
 * 收到客户端消息
 * 
 * @author lzb
 *
 */
@Service("PUBLISH")
@Slf4j
public class Publish implements IMqttReadService {

	@Autowired
	PubMsgMapper pubMsgMapper;

	@Override
	public void run(Channel channel, MqttMessage mqttMessage) {
		MqttPublishMessage msg = (MqttPublishMessage) mqttMessage;

		String clientId = channel.attr(MyNettyContext.CLIENT_ID_KEY).get();
		if (UtilString.isBlank(clientId)) {

		}

		PubMsg pubMsg = new PubMsg();
		pubMsg.set_id(new ObjectId());
		pubMsg.setClientId(clientId);
		pubMsg.setLastTime(new Date());
		pubMsg.setPacketId(msg.variableHeader().packetId());
		pubMsg.setQos(msg.fixedHeader().qosLevel().value());
		pubMsg.setTopic(msg.variableHeader().topicName());
		pubMsg.setAckCount(0);

		byte[] messageBytes = new byte[msg.payload().readableBytes()];
		msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
		pubMsg.setPayload(new String(messageBytes));

		// QoS=0
		if (msg.fixedHeader().qosLevel() == MqttQoS.AT_MOST_ONCE) {

		}
		// QoS=1
		if (msg.fixedHeader().qosLevel() == MqttQoS.AT_LEAST_ONCE) {
			MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE,
					false, 0);
			MqttPubAckMessage message = new MqttPubAckMessage(mqttFixedHeader,
					MqttMessageIdVariableHeader.from(msg.variableHeader().packetId()));
			channel.writeAndFlush(message);
			pubMsg.setAck(MqttMessageType.PUBACK.value());
		}
		// QoS=2
		if (msg.fixedHeader().qosLevel() == MqttQoS.EXACTLY_ONCE) {
			MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE,
					false, 0);
			MqttPubAckMessage message = new MqttPubAckMessage(mqttFixedHeader,
					MqttMessageIdVariableHeader.from(msg.variableHeader().packetId()));
			channel.writeAndFlush(message);
			pubMsg.setAck(MqttMessageType.PUBREC.value());
		}

		pubMsgMapper.insert(pubMsg);
		// 消息推送出去
		MyNettyContext.send(msg.variableHeader().topicName(), messageBytes, pubMsg.get_id());

		// PUBLISH -> PUBREC ->PUBREL->PUBCOMP

	}

}
