package com.lzb.rock.redis.mapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.lzb.rock.redis.helper.BloomFilterHelper;

/**
 * redis操作布隆过滤器
 * 
 * @param <T>
 * @author xhj
 */
@Component
public class RedisBloomFilterMapper {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	
	@Autowired
	BloomFilterHelper<String> bloomFilterHelper;

	/**
	 * 删除缓存的KEY
	 * 
	 * @param key KEY
	 */
	public void delete(String key) {

		redisTemplate.delete(key);
	}

	/**
	 * 根据给定的布隆过滤器添加值，在添加一个元素的时候使用，批量添加的性能差
	 *
	 * @param bloomFilterHelper 布隆过滤器对象
	 * @param key               KEY
	 * @param value             值
	 * @param <T>               泛型，可以传入任何类型的value
	 */
	public void add(String key, String value) {
		int[] offset = bloomFilterHelper.murmurHashOffset(value);
		for (int i : offset) {
			redisTemplate.opsForValue().setBit(key, i, true);
		}
	}

	/**
	 * 根据给定的布隆过滤器添加值，在添加一批元素的时候使用，批量添加的性能好，使用pipeline方式(如果是集群下，请使用优化后RedisPipeline的操作)
	 *
	 * @param bloomFilterHelper 布隆过滤器对象
	 * @param key               KEY
	 * @param valueList         值，列表
	 */
	public void addList(String key, List<String> valueList) {
		redisTemplate.executePipelined(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				connection.openPipeline();
				for (String value : valueList) {
					int[] offset = bloomFilterHelper.murmurHashOffset(value);
					for (int i : offset) {
						connection.setBit(key.getBytes(), i, true);
					}
				}
				return null;
			}
		});
	}

	/**
	 * 根据给定的布隆过滤器判断值是否存在
	 *
	 * @param bloomFilterHelper 布隆过滤器对象
	 * @param key               KEY
	 * @param value             值
	 * @param <T>               泛型，可以传入任何类型的value
	 * @return 是否存在
	 */
	public boolean contains(String key, String value) {
		int[] offset = bloomFilterHelper.murmurHashOffset(value);
		for (int i : offset) {
			if (!redisTemplate.opsForValue().getBit(key, i)) {
				return false;
			}
		}
		return true;
	}
}
