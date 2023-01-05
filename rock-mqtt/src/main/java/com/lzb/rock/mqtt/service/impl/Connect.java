package com.lzb.rock.mqtt.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lzb.rock.mqtt.context.MyNettyContext;
import com.lzb.rock.mqtt.mapper.ClientMapper;
import com.lzb.rock.mqtt.mapper.SendMsgMapper;
import com.lzb.rock.mqtt.model.Client;
import com.lzb.rock.mqtt.model.SendMsg;
import com.lzb.rock.mqtt.service.IMqttReadService;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;

@Service("CONNECT")
@Slf4j
public class Connect implements IMqttReadService {

	@Autowired
	ClientMapper clientMapper;

	@Autowired
	SendMsgMapper sendMsgMapper;

	@Override
	public void run(Channel channel, MqttMessage mqttMessage) {

		MqttConnectMessage msg = (MqttConnectMessage) mqttMessage;
		String userName = msg.payload().userName();
		byte[] passwordInBytes = msg.payload().passwordInBytes();
		String clientId = msg.payload().clientIdentifier();
		// String password = new String(msg.payload().passwordInBytes());
		log.info("建立新连接，userName:{};password:{};clientIdentifier:{}", userName, "", clientId);

		MqttFixedHeader connackFixedHeader = new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE,
				false, 0);
		MqttConnAckVariableHeader mqttConnAckVariableHeader = new MqttConnAckVariableHeader(
				MqttConnectReturnCode.CONNECTION_ACCEPTED, false);
		MqttConnAckMessage message = new MqttConnAckMessage(connackFixedHeader, mqttConnAckVariableHeader);
		channel.writeAndFlush(message);
		boolean isCleanSession = msg.variableHeader().isCleanSession();

		channel.attr(MyNettyContext.CLIENT_ID_KEY).set(clientId);
		channel.attr(MyNettyContext.CLEAN_SESSION_KEY).set(isCleanSession);
		MyNettyContext.putAll(clientId, channel);

		Client client = new Client();
		client.setClientId(clientId);
		client.setUserName(userName);
		client.setLastTime(new Date());
		client.setStatus(0);
		client.setCleanSession(isCleanSession);
		if (passwordInBytes != null && passwordInBytes.length > 0) {
			client.setPassword(new String(passwordInBytes));
		}
		clientMapper.upsertByClientId(client);

		// 不清除session,查询老的订阅关系加进去,同时处理离线消息
		if (!isCleanSession) {
			manageOld(channel, clientId);
		}

	}

	private void manageOld(Channel channel, String clientId) {

		Client clientOld = clientMapper.findByClientId(clientId);
		if (clientOld == null) {
			log.info("clientOld 为空;clientId:{}",clientId);
			return;
		}

		Map<String, Integer> topicMap = clientOld.getTopicMap();
		if (topicMap == null) {
			log.info("topicMap 为空;clientId:{}",clientId);
			return;
		}

		channel.attr(MyNettyContext.TOPIC_KEY).set(topicMap);
		
		for (Entry<String, Integer> entry : topicMap.entrySet()) {
			MyNettyContext.subscribe(entry.getKey(), clientId);
		}

		List<SendMsg> list = sendMsgMapper.findByClientIdAndAck(clientId, -1);
		for (SendMsg sendMsg : list) {
			MyNettyContext.send(sendMsg);
		}

	}

}
