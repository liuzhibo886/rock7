package com.lzb.rock.ehcache.aop.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 删除缓存
 * 
 * @author lzb 2018年7月11日 上午10:22:43
 */

@Inherited
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EhcacheDel {
	/**
	 * key 生成常量
	 * 
	 * @return
	 */
	String constant();

	/**
	 * 参数名称
	 * 
	 * @return
	 */
	String[] parameters() default {};// 参数

	/**
	 * cache 名字,ehcache.xml 中配置的name 值
	 */
	String cacheName();

}
