package com.lzb.rock.mqtt.runner;


import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.lzb.rock.mqtt.threads.DelayThread;

import lombok.extern.slf4j.Slf4j;


@Component
@Async
@Slf4j
public class DelayStartRunner implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		log.info("DelayThread 启动成功");
		DelayThread delayThread = new DelayThread();
		delayThread.start();
	}

}
