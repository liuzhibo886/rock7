package com.lzb.rock.base.demo;

import java.util.concurrent.DelayQueue;

import com.lzb.rock.base.model.MyDelayed;
import com.lzb.rock.base.util.UtilDate;

/**
 * jdk 自带 延时队列
 * 
 * @author lzb
 *
 */
public class DelayQueueDemo {

	static DelayQueue<MyDelayed<String>> delayQueue = new DelayQueue<MyDelayed<String>>();

	public static void main(String[] args) throws InterruptedException {
		System.out.println(UtilDate.getFomtTimeByDateString());

		MyDelayed<String> m = new MyDelayed<String>("1", 1000L * 10L);
		delayQueue.add(m);
		Runnable delayRun = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						MyDelayed<String> m2 = delayQueue.take();
						System.out.println(UtilDate.getFomtTimeByDateString() + "==>" + m2.getData());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		};

		Thread t = new Thread(delayRun);
	//	t.setDaemon(true);
		t.start();
	}

}
