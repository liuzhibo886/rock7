package com.lzb.rock.base.aop.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * beetl自动注册注解
 * 从spring容器中获取添加该注解的对象,自动加载至beetl模板中,可以在beetl中调用
 * @author lzb
 *
 * 2018年9月10日 下午8:13:10
 */
@Inherited
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Beetl {
	
	String name();
	
}
