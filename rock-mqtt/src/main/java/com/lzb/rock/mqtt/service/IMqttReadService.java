package com.lzb.rock.mqtt.service;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;

public interface IMqttReadService {
	
	public void run(Channel channel, MqttMessage mqttMessage);

}
