package com.lzb.rock.mqtt.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lzb.rock.mqtt.context.MyNettyContext;
import com.lzb.rock.mqtt.mapper.ClientMapper;
import com.lzb.rock.mqtt.model.Client;
import com.lzb.rock.mqtt.service.IMqttReadService;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * 取消订阅
 * 
 * @author lzb
 *
 */
@Service("UNSUBSCRIBE")
@Slf4j
public class UnSubscribe implements IMqttReadService {

	@Autowired
	ClientMapper clientMapper;

	@Override
	public void run(Channel channel, MqttMessage mqttMessage) {

		MqttUnsubscribeMessage mqttUnsubscribeMessage = (MqttUnsubscribeMessage) mqttMessage;
		String clientId = channel.attr(MyNettyContext.CLIENT_ID_KEY).get();
		Client client = clientMapper.findByClientId(clientId);
		log.info("client:{}", client);

	}

}
