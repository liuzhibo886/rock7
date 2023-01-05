package com.lzb.rock.rocketmq.test;

import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import lombok.extern.slf4j.Slf4j;

/**
 * 并发消费
 * 
 * @author lzb
 * @date 2020年8月25日上午10:26:46
 */
@Slf4j
public class MyMessageListenerConcurrently implements MessageListenerConcurrently {

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

		for (MessageExt messageExt : msgs) {
			log.info("messageExt:{}", new String(messageExt.getBody()));
		}
		log.info("MyMessageListenerConcurrently===>{};{}", msgs, context);
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

}
