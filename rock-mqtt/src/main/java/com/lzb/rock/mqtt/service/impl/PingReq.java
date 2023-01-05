package com.lzb.rock.mqtt.service.impl;

import org.springframework.stereotype.Service;

import com.lzb.rock.mqtt.service.IMqttReadService;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;

/**
 * 心跳
 * 
 * @author lzb
 *
 */
@Service("PINGREQ")
@Slf4j
public class PingReq implements IMqttReadService {

	@Override
	public void run(Channel channel, MqttMessage mqttMessage) {
		MqttFixedHeader pingreqFixedHeader = new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE,
				false, 0);
		MqttMessage pingResp = new MqttMessage(pingreqFixedHeader);
		channel.writeAndFlush(pingResp);
	}

}
