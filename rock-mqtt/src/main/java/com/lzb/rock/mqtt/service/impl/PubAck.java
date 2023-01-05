package com.lzb.rock.mqtt.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lzb.rock.mqtt.context.MyNettyContext;
import com.lzb.rock.mqtt.mapper.SendMsgMapper;
import com.lzb.rock.mqtt.service.IMqttReadService;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * 收到客户端消息回执
 * 
 * @author lzb
 *
 */
@Service("PUBACK")
@Slf4j
public class PubAck implements IMqttReadService {

	@Autowired
	SendMsgMapper sendMsgMapper;

	@Override
	public void run(Channel channel, MqttMessage mqttMessage) {

		MqttPubAckMessage msg = (MqttPubAckMessage) mqttMessage;

		String clientId = channel.attr(MyNettyContext.CLIENT_ID_KEY).get();
		int messageId = msg.variableHeader().messageId();
		sendMsgMapper.updateMultiAck(clientId, messageId, MqttMessageType.PUBLISH.value(), MqttMessageType.PUBACK.value());
	}

}
