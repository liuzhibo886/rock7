package com.lzb.rock.base.facade;

import java.util.List;

import org.bson.types.ObjectId;

/**
 * MQ消息基类
 * 
 * @author lzb
 * @date 2020年8月26日上午11:24:27
 */
public interface IMqMapper {

	/**
	 * 发送消息
	 * 
	 * @date 2020年8月29日上午10:32:18
	 * @param body
	 * @param mqEnum
	 * @return
	 */
	public String sendMsg(String body, IMqEnum mqEnum);

	public String sendMsg(String body, String topic, String tag);

	/**
	 * 发送多条消息
	 * 
	 * @date 2020年9月2日上午11:22:06
	 * @param bodys
	 * @param mqEnum
	 * @return
	 */
	public void sendMsg(List<String> bodys, IMqEnum mqEnum);

	/**
	 * 发送单向消息
	 * 
	 * @date 2020年8月29日上午10:32:34
	 * @param msg
	 */
	public void sendOneWayMsg(String body, IMqEnum mqEnum);

	/**
	 * 发送延时消息
	 * 
	 * @date 2020年8月29日上午10:31:35
	 * @param topic
	 * @param tag
	 * @param msg
	 * @param delayTime 延时时间 ,毫秒
	 * @return
	 */
	public String sendDelayMsg(String body, IMqEnum mqEnum, Long delayTime);

	/**
	 * send delay msg.
	 *
	 * @param msg        content
	 * @param delayLevel 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
	 * @return send result
	 */

	public String sendDelayMsg(String body, IMqEnum mqEnum, IMqDelayLevelEnum delayLevel);

	/**
	 * 删除消息
	 * @param msgId
	 * @param delayId
	 * @return
	 */
	public String delMsg(String msgId, ObjectId delayId);

	public void destroy();

}
