package com.lzb.rock.sharding.aop.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分片表
 * 
 * @author lzb
 *
 */
@Inherited
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ShardingRule {

	/**
	 * 逻辑表名称
	 * 
	 * @return
	 */
	public String logicTable();

	/**
	 * 主键ID
	 * 
	 * @return
	 */
	public String idColumn();

	/**
	 * 由数据源名 +
	 * 表名组成，以小数点分隔。多个表以逗号分隔，支持inline表达式。缺省表示使用已知数据源与逻辑表名称生成数据节点，用于广播表（即每个库中都需要一个同样的表用于关联查询，多为字典表）或只分库不分表且所有库的表结构完全一致的情况
	 * 
	 * @return
	 */
	public String actualDataNodes();
}
