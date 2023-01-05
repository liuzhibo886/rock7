package com.lzb.rock.base.aop.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.lzb.rock.base.enums.ConsumeFromEnum;
import com.lzb.rock.base.enums.MessageEnum;

/**
 * MQ 标记类
 * 
 * @author lzb 2018年4月16日 下午4:46:11
 */
@Inherited
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqProperties {
	/**
	 * 消费者线程数量,初始值
	 * 
	 * @date 2020年8月29日上午11:52:55
	 * @return
	 */
	int consumeThreadMin() default 0;

	/**
	 * 消费者线程数量 最大值
	 * 
	 * @date 2020年8月29日上午11:53:06
	 * @return
	 */
	int consumerConsumeThreadMax() default 0;

	/**
	 * 设置一次消费消息的条数，默认为1条
	 * 
	 * @date 2020年8月29日上午11:53:17
	 * @return
	 */
	int consumeMessageBatchMaxSiz() default 0;

	/**
	 * 消费类型
	 */
	MessageEnum messageEnum() default MessageEnum.CLUSTERING;

	/**
	 * 开始消费位置
	 * @date 2020年9月2日下午2:08:04
	 * @return
	 */
	ConsumeFromEnum consumeFromEnum() default ConsumeFromEnum.CONSUME_FROM_LAST_OFFSET;

}
