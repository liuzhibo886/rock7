package com.lzb.rock.web.config;

import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lzb.rock.web.properties.BeetlWebProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * web 配置类
 *
 * @author fengshuonan
 * @date 2016年11月12日 下午5:03:32
 */
@Configuration
@Slf4j
public class BeetlConfig {

	@Autowired
	BeetlWebProperties webProperties;

	@Value("${spring.mvc.view.prefix}")
	String prefix;

	/**
	 * beetl的配置
	 */
	@Bean(initMethod = "init", name = "beetlConfiguration")
	public BeetlConfiguration beetlConfiguration() {
		log.debug("BeetlConfiguration init ...");
		BeetlConfiguration beetlConfiguration = new BeetlConfiguration();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = BeetlConfig.class.getClassLoader();
		}
		beetlConfiguration.setResourceLoader(new ClasspathResourceLoader(loader, prefix));
		beetlConfiguration.setConfigProperties(webProperties.getProperties());
		return beetlConfiguration;
	}

	/**
	 * beetl的视图解析器
	 */
	@Bean
	public BeetlSpringViewResolver beetlViewResolver() {
		BeetlSpringViewResolver beetlSpringViewResolver = new BeetlSpringViewResolver();
		beetlSpringViewResolver.setConfig(beetlConfiguration());
		beetlSpringViewResolver.setContentType("text/html;charset=UTF-8");
		beetlSpringViewResolver.setOrder(0);
		return beetlSpringViewResolver;
	}
}
