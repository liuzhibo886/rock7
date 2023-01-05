package com.lzb.rock.rocketmq.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "rocketmq")
@Component
public class RocketMQProperties {
	/**
	 * #是否开启自动配置
	 */
	private boolean isEnable = false;
	/**
	 * #mq的nameserver地址
	 */
	private String namesrvAddr;
	/**
	 * #发送同一类消息的设置为同一个group，保证唯一,默认不需要设置，rocketmq会使用ip@pid(pid代表jvm名字)作为唯一标示
	 */
	private String groupName = "rock";
	/**
	 * #消息最大字节数 默认(4M)
	 */
	private int maxMessageSize = 1024 * 1024 * 4;
	/**
	 * 发送消息超时时间,默认3000,毫秒
	 */
	private int sendMsgTimeout = 30000;
	/**
	 * 发送失败重试另一个代理
	 */
	private boolean retryAnotherBrokerWhenNotStoreOK = false;
	/**
	 * 同步模式 发送消息失败重试次数，默认2
	 */
	private int retryTimesWhenSendFailed = 2;
	/**
	 * 异步模式 发送消息失败重试次数，默认2
	 */
	private int retryTimesWhenSendAsyncFailed = 2;
	/**
	 * 超过4K 开始压缩
	 */
	private int CompressMsgBodyOverHowmuch = 1024 * 4;
	/**
	 * #消费者线程数量,初始值
	 */
	private int consumerConsumeThreadMin = 5;
	/**
	 * #消费者线程数量 最大值
	 */
	private int consumerConsumeThreadMax = 30;
	/**
	 * #设置一次消费消息的条数，默认为1条
	 */
	private int consumeMessageBatchMaxSize = 10;

}
