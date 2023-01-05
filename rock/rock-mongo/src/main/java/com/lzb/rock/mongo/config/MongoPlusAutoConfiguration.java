package com.lzb.rock.mongo.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import com.lzb.rock.mongo.converter.BigDecimalToDecimal128Converter;
import com.lzb.rock.mongo.converter.Decimal128ToBigDecimalConverter;
import com.lzb.rock.mongo.properties.MongoOptionProperties;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;

@Configuration
@ConditionalOnClass(MongoClient.class)
@EnableConfigurationProperties(MongoOptionProperties.class)
@ConditionalOnMissingBean(type = "org.springframework.data.mongodb.MongoDbFactory")
public class MongoPlusAutoConfiguration {

	@Autowired
	MongoOptionProperties mongoOptionProperties;

	@Bean
	public MappingMongoConverter mappingMongoConverter(MongoMappingContext mongoMappingContext,
			MongoDatabaseFactory mongoDbFactory) throws Exception {
		DefaultDbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
		MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
		List<Object> list = new ArrayList<>();
		list.add(new BigDecimalToDecimal128Converter());// 自定义的类型转换器
		list.add(new Decimal128ToBigDecimalConverter());// 自定义的类型转换器
		converter.setCustomConversions(new MongoCustomConversions(list));
		return converter;
	}

	/**
	 * 连接池配置
	 * @return
	 */
	@Bean
	MongoClientSettings mongoClientSettings() {
		MongoClientSettings.Builder builder = MongoClientSettings.builder();
		builder.applyToConnectionPoolSettings(b -> {
			// 允许的最大连接数。这些连接在空闲时将保留在池中。一旦池耗尽，任何需要连接的操作都将阻塞等待可用连接 默认: 100
			b.maxSize(mongoOptionProperties.getMaxConnectionSize());
			// 最小连接数。这些连接在空闲时将保留在池中，并且池将确保它至少包含这个最小数量 默认: 0
			b.minSize(mongoOptionProperties.getMinConnectionSize());
			// 设置线程阻塞等待连接的最长时间，单位秒
			b.maxWaitTime(mongoOptionProperties.getMaxWaitTime(), TimeUnit.MINUTES);
			// 设置池连接的最大空闲时间。最大空闲时间，以秒为单位，必须大于等于0，0表示寿命没有限制
			b.maxConnectionIdleTime(mongoOptionProperties.getMaxConnectionIdleTime(), TimeUnit.MINUTES);
			// 设置池连接的最长生存时间，单位秒，小于等于0 为无限制
			b.maxConnectionLifeTime(mongoOptionProperties.getMaxConnectionLifeTime(), TimeUnit.MINUTES);

//			b.connectionsPerHost(mongoOptionProperties.getMaxConnectionPerHost());
//			b.threadsAllowedToBlockForConnectionMultiplier(
//					mongoOptionProperties.getThreadsAllowedToBlockForConnectionMultiplier());
//			b.serverSelectionTimeout(mongoOptionProperties.getServerSelectionTimeout())	;		
//			b.connectTimeout(mongoOptionProperties.getConnectTimeout());
//			b.socketTimeout(mongoOptionProperties.getSocketTimeout());
//			b.sslEnabled(mongoOptionProperties.getSslEnabled());
//			b.sslInvalidHostNameAllowed(mongoOptionProperties.getSslInvalidHostNameAllowed());
//			b.heartbeatFrequency(mongoOptionProperties.getHeartbeatFrequency());
//			b.minConnectionsPerHost(mongoOptionProperties.getMinConnectionPerHost());
//			b.heartbeatConnectTimeout(mongoOptionProperties.getHeartbeatConnectTimeout());
//			b.heartbeatSocketTimeout(mongoOptionProperties.getSocketTimeout());
//			b.localThreshold(mongoOptionProperties.getLocalThreshold());

		});
		return builder.build();
	}

}
