package com.lzb.rock.mqtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.lzb.rock.base.BaseApplication;

import lombok.extern.slf4j.Slf4j;

@EnableAutoConfiguration
@EnableScheduling
@ComponentScan(basePackages = { "com.lzb" })
@EnableFeignClients(basePackages = { "com.lzb" })
@EnableAsync
@Slf4j
public class MqttApplication extends BaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(MqttApplication.class, args);
		log.warn("==================启动成功==========================");
		log.info("==================启动成功==========================");
	}
}
