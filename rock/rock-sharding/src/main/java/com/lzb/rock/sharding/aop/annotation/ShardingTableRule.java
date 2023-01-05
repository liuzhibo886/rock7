package com.lzb.rock.sharding.aop.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;

/**
 * 分表规则
 * 
 * @author lzb
 *
 *         2020年12月23日 上午9:11:14
 *
 */
@Inherited
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ShardingTableRule {

	/**
	 * 分片列名称
	 * 
	 * @return
	 */
	public String ruleColumn();

	/**
	 * 分片算法行表达式，需符合groovy语法，详情请参考行表达式
	 * 
	 * @return
	 */
	public String algorithmExpression() default "";

	/**
	 * 自定义分库规则，优先于 algorithmExpression生效,精确分片算法，用于=和IN
	 * 
	 * @return
	 */
	Class<? extends PreciseShardingAlgorithm<?>>[] preciseShardingAlgorithm() default {};

	/**
	 * 范围分片算法，用于BETWEEN,必须先配置preciseShardingAlgorithm 才能生效
	 * 
	 * @return
	 */
	Class<? extends RangeShardingAlgorithm<?>>[] rangeShardingAlgorithm() default {};

}
