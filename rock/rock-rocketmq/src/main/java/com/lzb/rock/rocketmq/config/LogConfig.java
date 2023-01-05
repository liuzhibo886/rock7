package com.lzb.rock.rocketmq.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import lombok.extern.slf4j.Slf4j;

/**
 * 消费者
 * 
 * @author lzb
 * @date 2020年8月26日上午11:53:42
 */
@Slf4j
@Configuration
@Order(value = Integer.MIN_VALUE)
public class LogConfig implements ApplicationRunner {
	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.setProperty("rocketmq.client.logUseSlf4j", "true");
	}
}
