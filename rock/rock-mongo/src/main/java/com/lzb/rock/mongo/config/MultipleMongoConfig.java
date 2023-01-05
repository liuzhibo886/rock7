package com.lzb.rock.mongo.config;

import java.lang.reflect.Field;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.lzb.rock.base.util.UtilClass;
import com.lzb.rock.mongo.properties.MongoOptionProperties;
import com.lzb.rock.mongo.properties.MultipleMongoProperties;
import com.mongodb.ConnectionString;

import lombok.extern.slf4j.Slf4j;

/**
 * 多数据源开启
 * 
 * @author lzb
 * @date 2020年9月9日下午2:49:16
 */
@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "mongo", value = "isMultiple", havingValue = "true")
public class MultipleMongoConfig {

	@Autowired
	MultipleMongoProperties multipleMongoProperties;

	@Autowired
	MongoOptionProperties mongoOptionProperties;

	@Bean
	@Primary
	public MongoTemplate onApplicationEvent(@Autowired ApplicationContext ctx) {
		MongoTemplate mongoTemplateDefaul = null;
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) ctx.getAutowireCapableBeanFactory();

		for (Entry<String, String> entry : multipleMongoProperties.getMongodb().entrySet()) {
			if ("uri".equals(entry.getKey())) {
				SimpleMongoClientDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(
						new ConnectionString(getConnectionString(entry.getValue())));
				mongoTemplateDefaul = new MongoTemplate(factory);
				continue;
			}
			String[] arr = entry.getKey().split("\\.");
			String dbName = arr[0];
			String url = multipleMongoProperties.getMongodb().get(entry.getKey());
			// 创建mongodb
			SimpleMongoClientDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(
					new ConnectionString(getConnectionString(url)));
			MongoTemplate mongoTemplate = new MongoTemplate(factory);
			// 注入对象
			beanFactory.registerSingleton(dbName, mongoTemplate);
			log.info("注入多数据源：dbName:{}", dbName);
		}

		return mongoTemplateDefaul;
	}

	public String getConnectionString(String uri) {
		Field[] fields = UtilClass.getDeclaredFields(MongoOptionProperties.class);
		StringBuffer sb = new StringBuffer();
		for (Field field : fields) {
			Object value = UtilClass.getFieldValueObj(field.getName(), mongoOptionProperties);
			if (value != null) {
				sb.append("&").append(field.getName()).append("=").append(value);
			}
		}
		if (uri.indexOf("?") > -1) {
			if (uri.endsWith("?")) {
				uri = uri + sb.substring(1);
			} else {
				uri = uri + sb.toString();
			}
		} else {
			uri = uri + "?" + sb.substring(1);
		}
		return uri;
	}
}
