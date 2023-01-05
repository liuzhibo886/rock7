package com.lzb.rock.base.config;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * fastjson 定义
 * 
 * @author lzb
 *
 *         2018年11月7日 下午4:04:55
 */
@Configuration
public class Fastjsoncfg {

	/**
	 * 序列化配置
	 */
	@Bean
	public HttpMessageConverters fastjsonHttpMessageConverters() {
		/**
		 * objectId 转换
		 */
		HttpMessageConverters converters = new HttpMessageConverters(FastJsonHttpMessageConverterCfg.getInstance());

		return converters;

	}
}