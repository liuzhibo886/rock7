package com.lzb.rock.mqtt.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lzb.rock.mqtt.context.MyNettyContext;
import com.lzb.rock.mqtt.mapper.SendMsgMapper;
import com.lzb.rock.mqtt.service.IMqttReadService;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;

/**
 * 收到客户端消息
 * 
 * @author lzb
 *
 */
@Service("PUBREC")
@Slf4j
public class PubRec implements IMqttReadService {

	@Autowired
	SendMsgMapper sendMsgMapper;

	@Override
	public void run(Channel channel, MqttMessage mqttMessage) {

		MqttMessage message = MqttMessageFactory.newMessage(
				new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_MOST_ONCE, false, 0),
				MqttMessageIdVariableHeader
						.from(((MqttMessageIdVariableHeader) mqttMessage.variableHeader()).messageId()),
				null);
		channel.writeAndFlush(message);

		MqttMessageIdVariableHeader mqttMessageIdVariableHeader = (MqttMessageIdVariableHeader) mqttMessage
				.variableHeader();
		String clientId = channel.attr(MyNettyContext.CLIENT_ID_KEY).get();
		int packetId = mqttMessageIdVariableHeader.messageId();

		sendMsgMapper.updateMultiAck(clientId, packetId, MqttMessageType.PUBLISH.value(), MqttMessageType.PUBREL.value());
	}

}
