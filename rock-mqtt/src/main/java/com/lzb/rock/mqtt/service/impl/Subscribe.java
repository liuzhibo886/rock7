package com.lzb.rock.mqtt.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lzb.rock.mqtt.context.MyNettyContext;
import com.lzb.rock.mqtt.mapper.ClientMapper;
import com.lzb.rock.mqtt.model.Client;
import com.lzb.rock.mqtt.service.IMqttReadService;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubAckPayload;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import lombok.extern.slf4j.Slf4j;

/**
 * 订阅
 * 
 * @author lzb
 *
 */
@Service("SUBSCRIBE")
@Slf4j
public class Subscribe implements IMqttReadService {

	@Autowired
	ClientMapper clientMapper;

	@Override
	public void run(Channel channel, MqttMessage mqttMessage) {

		log.info("收到订阅消息{}", mqttMessage);

		MqttSubscribeMessage mqttSubscribeMessage = (MqttSubscribeMessage) mqttMessage;

		MqttFixedHeader subAckFixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_LEAST_ONCE,
				false, 0);
		MqttSubAckPayload payload = new MqttSubAckPayload(mqttMessage.fixedHeader().qosLevel().value());
		MqttSubAckMessage subAckMsg = new MqttSubAckMessage(subAckFixedHeader,
				(MqttMessageIdVariableHeader) mqttMessage.variableHeader(), payload);
		channel.writeAndFlush(subAckMsg);

		List<MqttTopicSubscription> topicSubscriptions = mqttSubscribeMessage.payload().topicSubscriptions();
		String clientId = channel.attr(MyNettyContext.CLIENT_ID_KEY).get();

		if (topicSubscriptions == null || topicSubscriptions.size() < 1) {
			log.info("订阅列表为空clientId:{}", clientId);
			return;
		}

		Map<String, Integer> topicMap = new HashMap<String, Integer>();
		for (MqttTopicSubscription mqttTopicSubscription : topicSubscriptions) {
			topicMap.put(mqttTopicSubscription.topicName(), mqttTopicSubscription.qualityOfService().value());
			MyNettyContext.subscribe(mqttTopicSubscription.topicName(), clientId);
		}

		channel.attr(MyNettyContext.TOPIC_KEY).set(topicMap);

		Client client = new Client();
		client.setClientId(clientId);
		client.setLastTime(new Date());
		client.setTopicMap(topicMap);
		clientMapper.upsertByClientId(client);
		
		

	}

}
