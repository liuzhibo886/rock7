package com.lzb.rock.base.model;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class MyDelayed<T> implements Delayed {

	T data;

	/**
	 * 到期时间，根据延时计算的结果
	 */
	private Long duration;

	/**
	 * 延迟时间,毫秒
	 */
	Long dueTime;

	/**
	 * 
	 * @param data    自定义数据,触发回调获取
	 * @param dueTime 延迟时间
	 */
	public MyDelayed(T data, Long dueTime) {
		this.data = data;
		this.dueTime = dueTime;
		this.duration = System.currentTimeMillis() + dueTime;
	}

	public void rest() {
		this.duration = System.currentTimeMillis() + this.dueTime;
	}

	public T getData() {

		return this.data;
	}

	public Long getDueTime() {
		return this.duration;
	}

	@Override
	public int compareTo(Delayed o) {
		Integer flag = 0;
		long compare = this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
		if (compare > 0) {
			flag = 1;
		}
		if (compare < 0) {
			flag = -1;
		}

		return flag;
	}

	@Override
	public long getDelay(TimeUnit unit) {

		return unit.convert(this.duration - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}

}
