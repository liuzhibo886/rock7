package com.lzb.rock.rocketmq.test;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import com.lzb.rock.rocketmq.properties.RocketMQProperties;

/**
 * 生产者
 * 
 * @author lzb
 * @date 2020年8月25日上午10:35:34
 */
public class ProducerTest {

	public static void main(String[] args) throws Exception {

		RocketMQProperties rocketMQProperties = new RocketMQProperties();

		String topic = "TopicTest";
		String tag = "Tag1";

		DefaultMQProducer producer = new DefaultMQProducer(rocketMQProperties.getGroupName());
		producer.setNamesrvAddr(rocketMQProperties.getNamesrvAddr());
		// 如果需要同一个jvm中不同的producer往不同的mq集群发送消息，需要设置不同的instanceName
		// producer.setInstanceName(instanceName);
		producer.setMaxMessageSize(rocketMQProperties.getMaxMessageSize());
		producer.setSendMsgTimeout(rocketMQProperties.getSendMsgTimeout());
		// 如果发送消息失败，设置重试次数，默认为2次
		producer.setRetryTimesWhenSendFailed(rocketMQProperties.getRetryTimesWhenSendFailed());
		producer.start();

		Long start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			for (int j = 0; j < 10; j++) {
				Message message = new Message(topic + j, tag, "12345", "rocketmq测试成功".getBytes());
				SendResult sendResult = producer.send(message);
				System.out.println("====>" + sendResult.getMsgId());
			}

		}
		Long end = System.currentTimeMillis();
		System.out.println(end - start);

	}

}
