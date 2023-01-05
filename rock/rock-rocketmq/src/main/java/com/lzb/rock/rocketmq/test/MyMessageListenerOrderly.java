package com.lzb.rock.rocketmq.test;

import java.util.List;

import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 有序消费
 * 
 * @author lzb
 * @date 2020年8月25日上午10:26:54
 */
public class MyMessageListenerOrderly implements MessageListenerOrderly {

	@Override
	public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
		
		
		return null;
	}

}
