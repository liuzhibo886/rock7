package com.lzb.rock.ehcache.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.sf.ehcache.CacheManager;

/**
 * https://blog.csdn.net/u014104286/article/details/79125141
 * https://wangxuliangboy.iteye.com/blog/647542
 * 
 * @author lzb
 * 
 *         2019年3月31日 下午11:20:00
 */
@Configuration
public class EhCacheConfig {

	@Value("${spring.profiles.active}")
	String active;

	/**
	 * EhCache的配置
	 */
	@Bean
	public CacheManager ehcache() {
		CacheManager create = CacheManager.create(this.getClass().getResourceAsStream("/ehcache-" + active + ".xml"));

//		EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
//		ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
		return create;
	}
}
