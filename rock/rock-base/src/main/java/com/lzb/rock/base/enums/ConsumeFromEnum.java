package com.lzb.rock.base.enums;

/**
 * 消息消费类型
 * 
 * @author lzb
 * @date 2020年9月2日下午2:03:03
 */
public enum ConsumeFromEnum {
	
	/**
	 * 从最后一个偏移量
	 */
	CONSUME_FROM_LAST_OFFSET,
	
    CONSUME_FROM_FIRST_OFFSET,
    
    CONSUME_FROM_TIMESTAMP,
}
