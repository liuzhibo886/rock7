package com.lzb.rock.base.util;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.StringUtils;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * http://www.heartthinkdo.com/?p=2457#12_MicrometerSpring_Boot_Actuator
 * https://www.cnblogs.com/duanxz/p/10179946.html
 * https://blog.csdn.net/qq_40994017/article/details/91419109
 * https://www.cnblogs.com/yunlongn/p/11343848.html
 * 
 * @author liuzhibo
 *
 *         2021年7月27日 上午11:44:13
 */
@Slf4j
public class UtilPrometheus {
	/**
	 * Gauge Gauge(仪表)是一个表示单个数值的度量， 它可以表示任意地上下移动的数值测量。 Gauge通常用于变动的测量值，如当前的内存使用情况，
	 * 同时也可以测量上下移动的"计数"，比如队列中的消息数量。
	 * 
	 * @param name     必填
	 * @param tagKey   选填
	 * @param tagValue 选填
	 * @return
	 */
	public static AtomicInteger initGauge(String name, String tagKey, String tagValue) {
		AtomicInteger number = new AtomicInteger(0);

		if (StringUtils.isEmpty(tagKey)) {
			AtomicInteger atomicInteger = Metrics.gauge(name, number);
			return atomicInteger;
		}
		ArrayList<Tag> tags = new ArrayList<Tag>();
		tags.add(new ImmutableTag(tagKey, tagValue));

//		Gauge.builder(name, number, AtomicInteger::get).tag(tagKey, tagValue).description("pass cases guage of demo")
//				.register(new SimpleMeterRegistry());

		AtomicInteger atomicInteger = Metrics.gauge(name, tags, number);
		// atomicInteger.incrementAndGet();
		// atomicInteger.set(newValue);
		return atomicInteger;
	}

	/**
	 * 记录XXX的总量或者计数值， 适用于一些增长类型的统计， 例如下单、支付次数、Http请求总量记录等等， 通过Tag可以区分不同的场景，对于下单，
	 * 可以使用不同的Tag标记不同的业务来源或者是按日期划分， 对于Http请求总量记录，可以使用Tag区分不同的URL
	 * 
	 * @return
	 */
	public static Counter initCounter(String name, String tagKey, String tagValue) {

		if (StringUtils.isEmpty(tagKey)) {
			Counter counter = Metrics.counter(name);
			return counter;
		}
		ArrayList<Tag> tags = new ArrayList<Tag>();
		tags.add(new ImmutableTag(tagKey, tagValue));
		Counter counter = Metrics.counter(name, tags);
		// counter.increment();

		return counter;
	}

	/**
	 * 
	 * @param name
	 * @param tagKey
	 * @param tagValue
	 * @return
	 */
	public static DistributionSummary initSum(String name, String tagKey, String tagValue) {

		ArrayList<Tag> tags = new ArrayList<Tag>();
		tags.add(new ImmutableTag(tagKey, tagValue));
		DistributionSummary summary = Metrics.summary(name, tags);
		// summary.histogramCountAtValue(value)

		return summary;
	}
	
}
