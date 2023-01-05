package com.lzb.rock.base.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.Decoder;
import feign.codec.Encoder;

/**
 * https://blog.csdn.net/u011687186/article/details/78457723?locationNum=5&fps=1
 * 
 * @author lzb feign 配置Fastjson系列化方法
 *
 */
@Configuration
public class FeignClientCfg {

	@Bean
	public Decoder feignDecoder() {
		ObjectFactory<HttpMessageConverters> objectFactory = new ObjectFactory<HttpMessageConverters>() {

			@Override
			public HttpMessageConverters getObject() throws BeansException {
				HttpMessageConverters httpMessageConverters = new HttpMessageConverters(
						FastJsonHttpMessageConverterCfg.getInstance());
				return httpMessageConverters;
			}
		};
		return new SpringDecoder(objectFactory);
	}

	@Bean
	public Encoder feignEncoder() {
		ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(
				FastJsonHttpMessageConverterCfg.getInstance());
		return new SpringEncoder(objectFactory);
	}
}
