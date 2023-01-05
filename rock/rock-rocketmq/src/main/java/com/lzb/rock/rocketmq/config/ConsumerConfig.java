package com.lzb.rock.rocketmq.config;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import com.lzb.rock.base.aop.annotation.MqProperties;
import com.lzb.rock.base.facade.IMqEnum;
import com.lzb.rock.rocketmq.facade.IRocketMqMessageListenerConcurrently;
import com.lzb.rock.rocketmq.facade.IRocketMqMessageListenerOrderBy;
import com.lzb.rock.rocketmq.properties.RocketMQProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * 消费者
 * 
 * @author lzb
 * @date 2020年8月26日上午11:53:42
 */
@Slf4j
@Configuration
public class ConsumerConfig implements ApplicationRunner {

	@Autowired(required = false)
	public List<IRocketMqMessageListenerConcurrently> rocketMqMessageListenerConcurrentlys;

	@Autowired(required = false)
	public List<IRocketMqMessageListenerOrderBy> rocketMqMessageListenerOrderBys;

	@Autowired
	RocketMQProperties rocketMQProperties;

	@Value("${spring.application.name}")
	String applicationName;

	public void init() throws Exception {
		
		log.info("rocketmq.client.logRoot===>{}", System.getProperty("rocketmq.client.logRoot"));

		/**
		 * 处理同步消息监听
		 */
		if (rocketMqMessageListenerConcurrentlys != null && rocketMqMessageListenerConcurrentlys.size() > 0) {

			for (IRocketMqMessageListenerConcurrently rockMessageListenerConcurrently : rocketMqMessageListenerConcurrentlys) {
				IMqEnum mqEnum = rockMessageListenerConcurrently.getMqEnum();
				MqProperties mqProperties = rockMessageListenerConcurrently.getClass()
						.getAnnotation(MqProperties.class);
				start(rockMessageListenerConcurrently, mqEnum, mqProperties);
			}

		} else {
			log.warn("=====>rocketMqMessageListenerConcurrentlys MQ 多线程消费者监听为空");
		}

		if (rocketMqMessageListenerOrderBys != null && rocketMqMessageListenerOrderBys.size() > 0) {

			for (IRocketMqMessageListenerOrderBy rocketMqMessageListenerOrderBy : rocketMqMessageListenerOrderBys) {
				IMqEnum mqEnum = rocketMqMessageListenerOrderBy.getMqEnum();
				MqProperties mqProperties = rocketMqMessageListenerOrderBy.getClass().getAnnotation(MqProperties.class);
				start(rocketMqMessageListenerOrderBy, mqEnum, mqProperties);
			}

		} else {
			log.warn("=====>rocketMqMessageListenerOrderBys MQ 顺序消费者监听为空");
		}
	}

	private void start(MessageListener messageListener, IMqEnum mqEnum, MqProperties mqProperties)
			throws MQClientException {
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
		consumer.setConsumerGroup(applicationName + "_" + mqEnum.getTopic());
		consumer.setNamesrvAddr(rocketMQProperties.getNamesrvAddr());

		// 设置集群消费
		if (mqProperties != null) {
			consumer.setMessageModel(MessageModel.valueOf(mqProperties.messageEnum().getModeCN()));
			consumer.setConsumeFromWhere(ConsumeFromWhere.valueOf(mqProperties.consumeFromEnum().name()));
		} else {
			consumer.setMessageModel(MessageModel.CLUSTERING);
			// 设置从最后一个偏移量消费
			consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		}

		consumer.subscribe(mqEnum.getTopic(), mqEnum.getTag());
		if (mqProperties != null && mqProperties.consumeThreadMin() > 0) {
			consumer.setConsumeThreadMin(mqProperties.consumeThreadMin());
		} else {
			consumer.setConsumeThreadMin(rocketMQProperties.getConsumerConsumeThreadMin());
		}

		if (mqProperties != null && mqProperties.consumerConsumeThreadMax() > 0) {
			consumer.setConsumeThreadMax(mqProperties.consumerConsumeThreadMax());
		} else {
			consumer.setConsumeThreadMax(rocketMQProperties.getConsumerConsumeThreadMax());
		}

		if (mqProperties != null && mqProperties.consumeMessageBatchMaxSiz() > 0) {
			consumer.setConsumeMessageBatchMaxSize(mqProperties.consumeMessageBatchMaxSiz());
		} else {
			consumer.setConsumeMessageBatchMaxSize(rocketMQProperties.getConsumeMessageBatchMaxSize());
		}
		consumer.registerMessageListener(messageListener);
		consumer.start();

	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		init();
	}

}
