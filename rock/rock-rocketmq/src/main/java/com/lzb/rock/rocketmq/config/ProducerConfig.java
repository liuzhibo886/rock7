package com.lzb.rock.rocketmq.config;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lzb.rock.rocketmq.maper.RocketMqMapper;
import com.lzb.rock.rocketmq.properties.RocketMQProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * 生产者
 * 
 * @author lzb
 * @date 2020年8月26日上午11:53:17
 */
@ConditionalOnProperty(prefix = "rocketmq", value = "isEnable", havingValue = "true")
@Configuration
@ConditionalOnClass({ DefaultMQPushConsumer.class })
@EnableConfigurationProperties(RocketMQProperties.class)
@Slf4j
public class ProducerConfig {

	@Autowired
	RocketMQProperties rocketMQProperties;

	@Bean("defaultMQProducer")
	@ConditionalOnClass(DefaultMQProducer.class)
	@ConditionalOnMissingBean(DefaultMQProducer.class)
	public DefaultMQProducer getDefaultMQProducer() {
		DefaultMQProducer producer = new DefaultMQProducer();
		producer.setProducerGroup(rocketMQProperties.getGroupName());
		producer.setNamesrvAddr(rocketMQProperties.getNamesrvAddr());

		// 如果需要同一个jvm中不同的producer往不同的mq集群发送消息，需要设置不同的instanceName
		// producer.setInstanceName("instanceName");
		producer.setMaxMessageSize(rocketMQProperties.getMaxMessageSize());
		producer.setSendMsgTimeout(rocketMQProperties.getSendMsgTimeout());
		// 如果发送消息失败，设置重试次数，默认为2次
		producer.setRetryTimesWhenSendFailed(rocketMQProperties.getRetryTimesWhenSendFailed());
		// 失败是否重试另一个代理
		producer.setRetryAnotherBrokerWhenNotStoreOK(rocketMQProperties.isRetryAnotherBrokerWhenNotStoreOK());
		/**
		 * 异步模式下,消息重发次数
		 */
		producer.setRetryTimesWhenSendAsyncFailed(rocketMQProperties.getRetryTimesWhenSendAsyncFailed());

		producer.setCompressMsgBodyOverHowmuch(rocketMQProperties.getCompressMsgBodyOverHowmuch());

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.info("rocketMQ producer 开始关闭");
			producer.shutdown();
			log.info("rocketMQ producer 关闭成功");
		}));

		try {
			producer.start();
			log.info("rocketmq 启动成功, namesrvAddr:{}, groupName:{}", rocketMQProperties.getNamesrvAddr(),
					rocketMQProperties.getGroupName());
		} catch (MQClientException e) {
			log.info("rocketmq 启动失败, namesrvAddr:{}, groupName:{}, ex:{}", rocketMQProperties.getNamesrvAddr(),
					rocketMQProperties.getGroupName(), e);
			e.printStackTrace();
		}

		return producer;

	}

//	@Bean(destroyMethod = "destroy")
//	@ConditionalOnBean(DefaultMQProducer.class)
//	@ConditionalOnMissingBean(name = "rocketMqMapper")
//	public RocketMqMapper defaultRocketMqProducer(@Qualifier("defaultMQProducer") DefaultMQProducer mqProducer) {
//		RocketMqMapper defaultMqMapper = new RocketMqMapper();
//		defaultMqMapper.setProducer(mqProducer);
//
//		return defaultMqMapper;
//	}

}
