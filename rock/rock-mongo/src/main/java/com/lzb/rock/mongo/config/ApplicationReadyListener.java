package com.lzb.rock.mongo.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;

/**
 * 去掉_class 字段
 * 
 * @author lzb
 * @date 2020年8月3日下午4:54:08
 */
@Configuration
public class ApplicationReadyListener implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	List<MongoTemplate> mongoTemplates;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		for (MongoTemplate mongoTemplate : mongoTemplates) {
			MongoConverter converter = mongoTemplate.getConverter();
			if (converter.getTypeMapper().isTypeKey("_class")) {
				((MappingMongoConverter) converter).setTypeMapper(new DefaultMongoTypeMapper(null));
			}
		}
	}
}