package com.lzb.rock.mongo.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 多数据源配置文件
 * 
 * @author lzb
 *
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data")
@EnableConfigurationProperties
@Data
public class MultipleMongoProperties {
	/**
	 * 从配置文件中读取的datasource开头的数据 注意：名称必须与配置文件中保持一致，下划线分割
	 */
	Map<String, String> mongodb = new HashMap<>();
}
