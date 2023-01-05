/**
 * @author lzb
 *
 * 
 *2019年5月12日 下午9:56:07
 */
package com.lzb.rock.web.config;

import java.util.Arrays;
import java.util.Properties;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.lzb.rock.base.xss.XssFilter;

/**
 * @author lzb
 * 
 *         2019年5月12日 下午9:56:07
 */
@Configuration
public class WebConfig {

	/**
	 * 验证码生成相关
	 */
	@Bean
	public DefaultKaptcha kaptcha() {
		Properties properties = new Properties();
		properties.put("kaptcha.border", "no");
		properties.put("kaptcha.border.color", "105,179,90");
		properties.put("kaptcha.textproducer.font.color", "blue");
		properties.put("kaptcha.image.width", "125");
		properties.put("kaptcha.image.height", "45");
		properties.put("kaptcha.textproducer.font.size", "45");
		properties.put("kaptcha.session.key", "code");
		properties.put("kaptcha.textproducer.char.length", "4");
		properties.put("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
		Config config = new Config(properties);
		DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
		defaultKaptcha.setConfig(config);
		return defaultKaptcha;
	}

	/**
	 * xssFilter注册
	 */
	@Bean
	public FilterRegistrationBean xssFilterRegistration() {
		XssFilter xssFilter = new XssFilter();
		xssFilter.setUrlExclusion(Arrays.asList("/notice/update", "/notice/add"));
		FilterRegistrationBean registration = new FilterRegistrationBean(xssFilter);
		registration.addUrlPatterns("/*");
		return registration;
	}
}
