package com.lzb.rock.web.config;

import org.beetl.ext.spring.BeetlGroupUtilConfiguration;

import com.lzb.rock.base.aop.annotation.Beetl;
import com.lzb.rock.base.holder.SpringContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * beetl拓展配置,绑定一些工具类,方便在模板中直接调用
 *
 * @author stylefeng
 * @Date 2018/2/22 21:03
 */
@Slf4j
public class BeetlConfiguration extends BeetlGroupUtilConfiguration {
	@Override
	public void initOther() {

		/**
		 * 获取所有交给spring的bean 找出有Beetl注解的类
		 */
		String[] beans = SpringContextHolder.getApplicationContext().getBeanDefinitionNames();
		for (String beanName : beans) {
			Class<?> beanType = SpringContextHolder.getApplicationContext().getType(beanName);
			Beetl beel = beanType.getAnnotation(Beetl.class);
			if (beel != null) {
				log.info("BeetlConfiguration=====>{}", beel.name());
				groupTemplate.registerFunctionPackage(beel.name(), SpringContextHolder.getBean(beanType));
			}
		}
		groupTemplate.registerFunctionPackage("log", log);
	}
}
