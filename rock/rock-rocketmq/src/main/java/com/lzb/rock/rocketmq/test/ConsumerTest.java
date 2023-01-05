package com.lzb.rock.rocketmq.test;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;

import com.lzb.rock.rocketmq.properties.RocketMQProperties;

/**
 * 消费者
 * https://www.cnblogs.com/SimpleWu/p/12112351.html#springboot%E7%8E%AF%E5%A2%83%E4%B8%AD%E4%BD%BF%E7%94%A8rocketmq
 * 
 * @author lzb
 * @date 2020年8月25日上午10:35:26
 */
public class ConsumerTest {

	public static void main(String[] args) throws Exception {

		RocketMQProperties rocketMQProperties = new RocketMQProperties();

		String topic = "TopicTest";
		String tag = "Tag1";

		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rocketMQProperties.getGroupName());
		consumer.setNamesrvAddr(rocketMQProperties.getNamesrvAddr());
		consumer.subscribe(topic, tag);
		consumer.setConsumeThreadMin(rocketMQProperties.getConsumerConsumeThreadMin());
		consumer.setConsumeThreadMax(rocketMQProperties.getConsumerConsumeThreadMax());
		consumer.setConsumeMessageBatchMaxSize(rocketMQProperties.getConsumeMessageBatchMaxSize());

		MyMessageListenerConcurrently myMessageListenerConcurrently = new MyMessageListenerConcurrently();
		consumer.registerMessageListener(myMessageListenerConcurrently);
		consumer.start();

	}

}
