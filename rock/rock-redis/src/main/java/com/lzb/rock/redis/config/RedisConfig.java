package com.lzb.rock.redis.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.lzb.rock.base.config.FastJsonHttpMessageConverterCfg;
import com.lzb.rock.redis.helper.BloomFilterHelper;

/**
 * Redis配置文件类
 * 
 * @author lzb https://blog.csdn.net/cadn_jueying/article/details/80736557
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

	@Bean
	@Primary
	public RedisTemplate<String, Object> stringSerializerRedisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		redisTemplate.setConnectionFactory(factory);

		RedisSerializer<String> stringSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringSerializer);
		redisTemplate.setHashKeySerializer(stringSerializer);

		FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<Object>(Object.class);
		fastJsonRedisSerializer.setFastJsonConfig(FastJsonHttpMessageConverterCfg.getInstance().getFastJsonConfig());

		redisTemplate.setValueSerializer(fastJsonRedisSerializer);
		redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);

		// 支持事务
		redisTemplate.setEnableTransactionSupport(true);

		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	@Bean
	public BloomFilterHelper<String> getBloomFilterHelper() {

		return new BloomFilterHelper<String>(10000 * 10000 * 4);
	}
}
