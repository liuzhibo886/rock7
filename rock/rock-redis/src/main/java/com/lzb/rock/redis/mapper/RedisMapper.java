package com.lzb.rock.redis.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.lzb.rock.base.util.UtilClass;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@SuppressWarnings("unchecked")
public class RedisMapper {

	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	public RedisTemplate<String, Object> redisTemplate() {
		return this.redisTemplate;
	}

	/**
	 * redis set缓存
	 * 
	 * @param <T>
	 * 
	 * @param key
	 * @param value
	 * @param timeout
	 */

	public <T> void set(String key, T value, long timeout) {
		if (timeout < 0) {
			redisTemplate.opsForValue().set(key, value);
		} else {
			redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
		}
	}

	/**
	 * 
	 * @param key
	 * @param obj 只能为对象
	 */

	public <T> void set(String key, T obj) {
		set(key, obj, 600);
	}

	/**
	 * 判断缓存是否存在
	 * 
	 * @param key
	 * @return
	 */

	public boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * 获取过期时间
	 * 
	 * @param key
	 * @return
	 */

	public Long getExpire(String key) {
		// 根据key获取过期时间
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 */

	public void del(String key) {
		redisTemplate.delete(key);
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 * @return
	 */
	public <T> T get(String key, Class<T> clazz) {
		Object obj = redisTemplate.opsForValue().get(key);

		return UtilClass.getJavaBean(obj, clazz);

	}

	public <T> List<T> getList(String key, Class<T> clazz) {
		Object obj = redisTemplate.opsForValue().get(key);
		if (obj == null) {
			return null;
		}
		return ((JSONArray) obj).toJavaList(clazz);
	}

	/**
	 * 对缓存数加减
	 * 
	 * @param key
	 * @param delta
	 * @return
	 */

	public Long increment(String key, long delta) {

		return redisTemplate.boundValueOps(key).increment(delta);
	}

	/**
	 * 对缓存数加减
	 * 
	 * @param key
	 * @param delta
	 * @return
	 */

	public Double increment(String key, Double delta) {

		return redisTemplate.boundValueOps(key).increment(delta);
	}

	/**
	 * 判断锁是否存在
	 */

	public boolean islock(String key) {
		key = getLockKey(key);
		return redisTemplate.hasKey(key);
	}

	/**
	 * 加锁 默认有效时间5分钟
	 * 
	 * @param key 锁唯一id
	 * @return
	 */

	public boolean lock(String key) {
		key = getLockKey(key);
		return lock(key, 60 * 5);
	}

	/**
	 * 
	 * @param key     锁key值
	 * @param count   抢几次
	 * @param timeout 锁的超时时间 秒
	 * @param millis  抢锁失败间隔
	 * @return
	 */
	public boolean robLock(String key, long timeout, Integer count, long millis) {
		key = getLockKey(key);
		for (int i = 0; i < count; i++) {
			if (lock(key, timeout)) {
				return true;
			}
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * 重置key时间
	 * 
	 * @date 2020年8月14日上午11:33:40
	 * @param key
	 * @param timeout
	 */

	public void expire(String key, long timeout) {
		if (redisTemplate.hasKey(key)) {
			redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		}
	}

	/**
	 * 重新设置锁的失效时间
	 * 
	 * @param key
	 * @param timeout
	 */

	public void lockExpire(String key, long timeout) {
		key = getLockKey(key);
		expire(key, timeout);
	}

	/**
	 * 加锁
	 * 
	 * @param key     锁唯一id
	 * @param timeout 超时时间，单位秒
	 * @return true成功， false失败
	 */

	public Boolean lock(String key, long timeout) {
		key = getLockKey(key);
		Boolean isLock = false;
		String uuid = UUID.randomUUID().toString();
		// 若不在，则设置值
		Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, uuid);
		if (flag && redisTemplate.hasKey(key)) {
			redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
			String newUuid = get(key, String.class);
			if (uuid.equals(newUuid)) {
				isLock = true;
			}
		}
		return isLock;
	}

	/**
	 * 加锁添加内容
	 * 
	 * @date 2020年8月5日下午2:03:53
	 * @param key
	 * @param timeout
	 * @param value
	 * @return
	 */

	public Boolean lock(String key, long timeout, String value) {
		key = getLockKey(key);
		Boolean isLock = false;
		// 若不在，则设置值
		Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, value);
		if (flag && redisTemplate.hasKey(key)) {
			redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
			String redisValue = get(key, String.class);
			if (value.equals(redisValue)) {
				isLock = true;
			}
		}
		return isLock;
	}

	/**
	 * 解锁
	 */

	public void unlock(String key) {
		key = getLockKey(key);
		redisTemplate.delete(key);
	}

	/**
	 * 生成分布式锁的key
	 * 
	 * @return
	 */

	public String getLockKey(String key) {
		if (!key.startsWith("LOCK_")) {
			key = "LOCK_" + key;
		}
		return key;
	}

	/**
	 * 获取key值
	 * 
	 * @param pattern
	 * @return
	 */
	public Set<String> keys(String pattern) {

		return redisTemplate.keys(pattern);
	}

	/**
	 * 移除指定key 的过期时间
	 *
	 * @param key
	 * @return
	 */
	public boolean persist(String key) {
		return redisTemplate.boundValueOps(key).persist();
	}

	/**
	 * key重命名
	 *
	 * @param oldName
	 * @param newName
	 */
	public void rename(String oldName, String newName) {
		redisTemplate.rename(oldName, newName);
	}

	// - - - - - - - - - - - - - - - - - - - - - String类型 - - - - - - - - - - - - -
	// - - - - - - -

	/**
	 * 根据key获取值
	 *
	 * @param keys 键
	 * @return 值
	 */
	public Set<String> getOfLike(String keys) {
		Set<String> set = redisTemplate.keys(keys);
		return set;
	}

	/**
	 * 根据key获取值
	 *
	 * @param key 键
	 * @return 值
	 */
	public <T> List<T> getAllByList(String key, Class<T> clazz) {
		if (key == null) {
			return null;
		}

		List<Object> list = redisTemplate.opsForList().range(key, 0L, -1L);
		if (list == null) {
			return new ArrayList<T>();
		}

		List<T> list2 = new ArrayList<T>(list.size());
		for (Object obj : list) {
			list2.add(UtilClass.getJavaBean(obj, clazz));
		}

		return list2;
	}

	/**
	 * 批量添加 key (重复的键会覆盖)
	 *
	 * @param keyAndValue
	 */
	public void setBatch(Map<String, String> keyAndValue) {
		redisTemplate.opsForValue().multiSet(keyAndValue);
	}

	/**
	 * 批量添加 key-value 只有在键不存在时,才添加 map 中只要有一个key存在,则全部不添加
	 *
	 * @param keyAndValue
	 */
	public void setBatchIfAbsent(Map<String, String> keyAndValue) {
		redisTemplate.opsForValue().multiSetIfAbsent(keyAndValue);
	}

	public <T> void setBySet(String key, T... t) {
		redisTemplate.opsForSet().add(key, t);
	}

	public <T> void setBySet(String key, List<T> list) {
		redisTemplate.opsForSet().add(key, list.toArray());
	}

	/**
	 * 获取变量中的值
	 *
	 * @param key 键
	 * @return
	 */

	public <T> Set<T> getBySet(String key, Class<T> clazz) {
		Set<Object> obj = redisTemplate.opsForSet().members(key);
		if (obj == null) {
			return null;
		}
		Set<T> set = new TreeSet<T>();
		for (Object obj2 : obj) {
			set.add(UtilClass.getJavaBean(obj2, clazz));
		}
		return set;
	}

	/**
	 * 随机获取set 变量的值
	 *
	 * @param key   键
	 * @param count 值
	 * @return
	 */
	public <T> List<T> getRandomBySet(String key, long count, Class<T> clazz) {
		List<Object> list = redisTemplate.opsForSet().randomMembers(key, count);
		if (list == null) {
			return null;
		}
		List<T> list2 = new ArrayList<T>(list.size());
		for (Object obj2 : list) {
			list2.add(UtilClass.getJavaBean(obj2, clazz));
		}
		return list2;
	}

	/**
	 * 随机获取变量中的元素
	 *
	 * @param key 键
	 * @return
	 */
	public <T> T getRandomBySet(String key, Class<T> clazz) {
		Object obj = redisTemplate.opsForSet().randomMember(key);
		if (obj == null) {
			return null;
		}
		return UtilClass.getJavaBean(obj, clazz);
	}

	/**
	 * 弹出变量中的元素
	 *
	 * @param key 键
	 * @return
	 */
	public <T> T popBySet(String key, Class<T> clazz) {
		Object obj = redisTemplate.opsForSet().pop(key);

		if (obj == null) {
			return null;
		}
		return UtilClass.getJavaBean(obj, clazz);
	}

	/**
	 * 获取变量中值的长度
	 *
	 * @param key 键
	 * @return
	 */
	public long sizeBySet(String key) {
		Long size = redisTemplate.opsForSet().size(key);
		return size == null ? 0 : size;
	}

	/**
	 * 获取变量中值的长度
	 *
	 * @param key 键
	 * @return
	 */
	public long sizeByZSet(String key) {
		return redisTemplate.opsForZSet().size(key);
	}

	/**
	 * 根据value从一个set中查询,是否存在
	 *
	 * @param key   键
	 * @param value 值
	 * @return true 存在 false不存在
	 */
	public <T> boolean isMemberBySet(String key, T value) {
		return redisTemplate.opsForSet().isMember(key, value);
	}

	/**
	 * 转移变量的元素值到目的变量。
	 *
	 * @param key     键
	 * @param value   元素对象
	 * @param destKey 元素对象
	 * @return
	 */
	public <V> boolean moveBySet(String key, V value, String destKey) {
		return redisTemplate.opsForSet().move(key, value, destKey);
	}

	/**
	 * 批量移除set缓存中元素
	 *
	 * @param key    键
	 * @param values 值
	 * @return
	 */
	public <V> void removeBySet(String key, V... values) {
		redisTemplate.opsForSet().remove(key, values);
	}

	/**
	 * 加入缓存
	 *
	 * @param key 键
	 * @param map 键
	 * @return
	 */
	public <K, V> void addByHash(String key, Map<K, V> map) {
		redisTemplate.opsForHash().putAll(key, map);
	}

	/**
	 * 加入缓存
	 * 
	 * @param <E>
	 *
	 * @param key 键
	 * @return
	 */
	public <V> void addByHash(String key, String hashKey, V value) {
		redisTemplate.opsForHash().put(key, hashKey, value);
	}

	/**
	 * 获取 key 下的 所有 hashkey 和 value
	 *
	 * @param key 键
	 * @return
	 */
	public <V> Map<String, V> getAllByHash(String key, Class<V> clazz) {
		Map<Object, Object> obj = redisTemplate.opsForHash().entries(key);
		if (obj == null) {
			return new HashMap<String, V>();
		}
		HashMap<String, V> map = new HashMap<String, V>();
		for (Entry<Object, Object> entry : obj.entrySet()) {
			map.put((String) entry.getKey(), UtilClass.getJavaBean(entry.getValue(), clazz));
		}

		return map;
	}

	/**
	 * 验证指定 key 下 有没有指定的 hashkey
	 *
	 * @param key
	 * @param hashKey
	 * @return
	 */
	public boolean hashKeyByHash(String key, String hashKey) {
		return redisTemplate.opsForHash().hasKey(key, hashKey);
	}

	/**
	 * 获取指定key的值string
	 *
	 * @param key     键
	 * @param hashKey 键
	 * @return
	 */
	public <V> V getByHash(String key, String hashKey, Class<V> clazz) {
		Object obj = redisTemplate.opsForHash().get(key, hashKey);
		if (obj == null) {
			return null;
		}
		return UtilClass.getJavaBean(obj, clazz);
	}

	/**
	 * 删除指定 hash 的 HashKey
	 *
	 * @param key
	 * @param hashKeys
	 * @return 删除成功的 数量
	 */
	public Long delete(String key, String... hashKey) {

		Long count = redisTemplate.opsForHash().delete(key, hashKey);

		return count;
	}

	/**
	 * 给指定 hash 的 hashkey 做增减操作
	 *
	 * @param key
	 * @param hashKey
	 * @param number
	 * @return
	 */
	public <K> Long incrementByHash(String key, K hashKey, Long number) {
		return redisTemplate.opsForHash().increment(key, hashKey, number);
	}

	/**
	 * 给指定 hash 的 hashkey 做增减操作
	 *
	 * @param key
	 * @param hashKey
	 * @param number
	 * @return
	 */
	public <K> Double incrementByHash(String key, K hashKey, Double number) {
		return redisTemplate.opsForHash().increment(key, hashKey, number);
	}

	/**
	 * 获取 key 下的 所有 hashkey 字段
	 *
	 * @param key
	 * @return
	 */
	public <V> Set<V> hashKeys(String key, Class<V> clazz) {
		Set<Object> set = redisTemplate.opsForHash().keys(key);
		if (set == null) {
			return null;
		}
		Set<V> set2 = new TreeSet<V>();
		for (Object obj2 : set) {
			set2.add(UtilClass.getJavaBean(obj2, clazz));
		}

		return set2;
	}

	/**
	 * 获取 key 下的 所有
	 *
	 * @param key
	 * @return
	 */
	public <V> Map<String, V> getAllByMap(String key, Class<V> clazz) {
		Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
		if (map == null) {
			return null;
		}

		HashMap<String, V> map2 = new HashMap<String, V>();
		for (Entry<Object, Object> entry : map.entrySet()) {
			map2.put((String) entry.getKey(), UtilClass.getJavaBean(entry.getValue(), clazz));
		}

		return map2;
	}

	/**
	 * 获取指定 hash 下面的 键值对 数量
	 *
	 * @param key
	 * @return
	 */
	public Long sizeByHash(String key) {
		return redisTemplate.opsForHash().size(key);
	}

	// - - - - - - - - - - - - - - - - - - - - - list类型 - - - - - - - - - - - - - -
	// - - - - - -

	/**
	 * 在变量左边添加元素值
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public void leftPushByList(String key, Object value) {
		redisTemplate.opsForList().leftPush(key, value);
	}

	/**
	 * 获取集合指定位置的值。
	 *
	 * @param key
	 * @param index
	 * @return
	 */
	public <T> T indexByList(String key, long index, Class<T> clazz) {

		Object obj = redisTemplate.opsForList().index(key, index);
		if (obj == null) {
			return null;
		}
		return UtilClass.getJavaBean(obj, clazz);
	}

	/**
	 * 获取指定区间的值。
	 *
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public <T> List<T> rangeByList(String key, long start, long end, Class<T> clazz) {
		List<Object> list = redisTemplate.opsForList().range(key, start, end);

		if (list == null) {
			return null;
		}

		List<T> listnew = new ArrayList<T>(list.size());
		for (Object object : list) {
			if (object == null) {
				listnew.add(null);
				continue;
			}
			listnew.add(UtilClass.getJavaBean(object, clazz));
		}

		return listnew;
	}

	/**
	 * 把最后一个参数值放到指定集合的第一个出现中间参数的前面， 如果中间参数值存在的话。
	 *
	 * @param key
	 * @param pivot
	 * @param value
	 * @return
	 */
	public <T> void leftPushByList(String key, T pivot, T value) {
		redisTemplate.opsForList().leftPush(key, pivot, value);
	}

	/**
	 * 向左边批量添加参数元素。
	 *
	 * @param key
	 * @param values
	 * @return
	 */
	public <T> void leftPushAllByList(String key, T... values) {
//        redisTemplate.opsForList().leftPushAll(key,"w","x","y");
		redisTemplate.opsForList().leftPushAll(key, values);
	}

	public <T> void leftPushAllByList(String key, List<T> values) {
		redisTemplate.opsForList().leftPushAll(key, values);
	}

	/**
	 * 向集合最右边添加元素。
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public <T> void rightPushByList(String key, T value) {
		redisTemplate.opsForList().rightPush(key, value);
	}

	/**
	 * 向左边批量添加参数元素。
	 *
	 * @param key
	 * @param values
	 * @return
	 */
	public <T> void rightPushAllByList(String key, T... values) {
		redisTemplate.opsForList().rightPushAll(key, values);
	}

	/**
	 * 向已存在的集合中添加元素。
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public <T> void rightPushIfPresentByList(String key, T value) {
		redisTemplate.opsForList().rightPushIfPresent(key, value);
	}

	/**
	 * 获取size 长度
	 *
	 * @param key
	 * @return
	 */
	public long sizeByList(String key) {
		return redisTemplate.opsForList().size(key);
	}

	/**
	 * 移除集合中的左边第一个元素。
	 *
	 * @param key
	 * @return
	 */
	public void leftPopByList(String key) {
		redisTemplate.opsForList().leftPop(key);
	}

	/**
	 * 移除集合中左边的元素在等待的时间里，如果超过等待的时间仍没有元素则退出。
	 *
	 * @param key
	 * @return
	 */
	public void leftPopByList(String key, long timeout, TimeUnit unit) {
		redisTemplate.opsForList().leftPop(key, timeout, unit);
	}

	/**
	 * 移除集合中右边的元素。
	 *
	 * @param key
	 * @return
	 */
	public void rightPopByList(String key) {
		redisTemplate.opsForList().rightPop(key);
	}

	/**
	 * 移除集合中右边的元素在等待的时间里，如果超过等待的时间仍没有元素则退出。
	 *
	 * @param key
	 * @return
	 */
	public void rightPopByList(String key, long timeout, TimeUnit unit) {
		redisTemplate.opsForList().rightPop(key, timeout, unit);
	}

	/**
	 * 删除list中指定的值
	 *
	 * @param key
	 * @param item
	 * @return
	 */
	public <T> Long removeListItemByList(String key, T item) {
		return redisTemplate.opsForList().remove(key, 0, item);
	}

	public void trimListItemByList(String key, long start, long end) {
		redisTemplate.opsForList().trim(key, start, end);
	}

	/**
	 * 通过key名来搜索有多少个类似名称的key,用searchKeyByScan 替代
	 *
	 * @param keyName
	 * @return
	 */
	public Set<String> searchKey(String keyName) {
		return searchKeyByScan(keyName);
	}

	/**
	 * scan 实现
	 * 
	 * @param pattern 表达式，如：abc*，找出所有以abc开始的键
	 */
	public Set<String> searchKeyByScan(String pattern) {
		return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
			Set<String> keysTmp = new HashSet<>();
			try (Cursor<byte[]> cursor = connection
					.scan(new ScanOptions.ScanOptionsBuilder().match(pattern).count(1000).build())) {
				while (cursor.hasNext()) {
					keysTmp.add(new String(cursor.next(), "Utf-8"));
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
			return keysTmp;
		});
	}

}
