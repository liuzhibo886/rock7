package com.lzb.rock.base.facade;

/**
 * MQ 枚举基类
 * 
 * @author lzb
 * @param <E>
 *
 * @param <E> 2019年3月17日 下午10:08:53
 */
public interface IMqEnum{

	public String getTopic();

	public String getTag();
}