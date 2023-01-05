package com.lzb.rock.sharding.aop.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * 
 * @author lzb
 * @Date 2020-12-27 12:02:29
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ShardingScan {

	// org.springframework.context.annotation.ConfigurationClassParser#doProcessConfigurationClass里面对于@ComponentScan注解的处

	String[] basePackages();

}
