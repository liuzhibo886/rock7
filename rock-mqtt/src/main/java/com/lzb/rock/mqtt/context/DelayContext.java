package com.lzb.rock.mqtt.context;

import java.util.concurrent.DelayQueue;

import org.bson.types.ObjectId;

import com.lzb.rock.base.model.MyDelayed;

public class DelayContext {
	
	private final static DelayQueue<MyDelayed<ObjectId>> delayQueue = new DelayQueue<MyDelayed<ObjectId>>();

	/**
	 * 获取到时的延迟消息
	 * 
	 * @return
	 */
	public static MyDelayed<ObjectId> take() {
		MyDelayed<ObjectId> myDelayed = null;

		try {
			myDelayed = delayQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return myDelayed;
	}

	/**
	 * 增加延迟消息
	 * 
	 * @param myDelayed
	 */

	public static void offer(MyDelayed<ObjectId> myDelayed) {
		myDelayed.rest();
		delayQueue.offer(myDelayed);
	}

}
