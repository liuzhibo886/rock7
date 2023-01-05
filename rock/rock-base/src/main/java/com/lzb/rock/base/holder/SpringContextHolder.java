package com.lzb.rock.base.holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * Spring的ApplicationContext的持有者,可以用静态方法的方式获取spring容器中的bean
 * 
 * @author lzb
 * 
 *         2019年4月8日 上午1:02:52
 */
@Component
@Lazy(false)
@Order(Integer.MIN_VALUE)
@Slf4j
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
	private static Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);
	private static ApplicationContext applicationContext;

	@Autowired
	SpringContextHolder springContextHolder;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextHolder.applicationContext = applicationContext;
		log.info("==================ApplicationContext加载成功==================");
	}

	/**
	 * 取得存储在静态变量中的ApplicationContext.
	 */
	public static ApplicationContext getApplicationContext() {
		assertApplicationContext();
		return applicationContext;
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName) {
		assertApplicationContext();
		try {
			return (T) applicationContext.getBean(beanName);
		} catch (Exception e) {
			log.debug("beanName实例不存在:{}", beanName);
		}
		return null;

	}

	public static <T> T getBean(String beanName, Class<T> requiredType) {
		assertApplicationContext();
		try {
			return (T) applicationContext.getBean(beanName, requiredType);
		} catch (Exception e) {
			log.debug("beanName实例不存在:{}", beanName);
		}
		return null;
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		assertApplicationContext();
		try {
			return applicationContext.getBean(requiredType);
		} catch (Exception e) {
			log.debug("requiredType实例不存在:{}", requiredType);
		}
		return null;
	}

	/**
	 * 清除SpringContextHolder中的ApplicationContext为Null.
	 */
	public static void clearHolder() {
		if (logger.isDebugEnabled()) {
			logger.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
		}
		applicationContext = null;
	}

	/**
	 * 实现DisposableBean接口, 在Context关闭时清理静态变量.
	 */
	@Override
	public void destroy() throws Exception {
		SpringContextHolder.clearHolder();
	}

	/**
	 * 判断对象是否为空
	 * 
	 * @return
	 */
	public static Boolean isNotNull() {

		return SpringContextHolder.applicationContext != null;
	}

	/**
	 * 检查ApplicationContext不为空.
	 */
	private static void assertApplicationContext() {
		if (SpringContextHolder.applicationContext == null) {
			throw new RuntimeException("applicaitonContext属性为null,请检查是否注入了SpringContextHolder!");
		}
	}

}
