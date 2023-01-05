package com.lzb.rock.rocketmq.facade;


import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;

import com.lzb.rock.base.facade.IMqEnum;

/**
 * 同步消费消息监听接口
 * 
 * @author lzb
 * @date 2020年8月29日上午10:34:16
 */
public interface IRocketMqMessageListenerOrderBy extends MessageListenerOrderly{

	/**
	 * 监听枚举
	 * 
	 * @date 2020年8月29日上午10:54:20
	 * @return
	 */
	public IMqEnum getMqEnum();
}
