package com.lzb.rock.sharding.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import com.lzb.rock.sharding.aop.annotation.ShardingScan;

import lombok.extern.slf4j.Slf4j;

//@Configuration
@Slf4j
public class MyBeanPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

		ShardingScan shardingScan = bean.getClass().getAnnotation(ShardingScan.class);
		if (shardingScan != null) {
			log.info("postProcessBeforeInitialization,beanName:{};basePackages:{}", beanName,
					shardingScan.basePackages());
		}

		return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// log.info("postProcessAfterInitialization,beanName:{}", beanName);
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}

}
