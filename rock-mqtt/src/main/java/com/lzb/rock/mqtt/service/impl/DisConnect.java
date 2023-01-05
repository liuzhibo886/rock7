package com.lzb.rock.mqtt.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lzb.rock.mqtt.context.MyNettyContext;
import com.lzb.rock.mqtt.mapper.ClientMapper;
import com.lzb.rock.mqtt.model.Client;
import com.lzb.rock.mqtt.service.IMqttReadService;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * 断开连接
 * 
 * @author lzb
 *
 */
@Service("DISCONNECT")
@Slf4j
public class DisConnect implements IMqttReadService {

	@Autowired
	ClientMapper clientMapper;

	@Override
	public void run(Channel channel, MqttMessage mqttMessage) {

		String clientId = channel.attr(MyNettyContext.CLIENT_ID_KEY).get();
		Client client = new Client();
		client.setClientId(clientId);
		client.setStatus(1);
		client.setLastTime(new Date());
		clientMapper.upsertByClientId(client);
		MyNettyContext.disConnect(channel);
		log.info("客户端断开连接,clientId:{}", clientId);
		channel.close();
	}

}
