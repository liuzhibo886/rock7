/**
 * @author lzb
 *
 * 
 *2019年4月1日 下午9:51:44
 */
package com.lzb.rock.ehcache.mapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * @author lzb
 * 
 *         2019年4月1日 下午9:51:44
 */
@Component
public class EhCacheMapper {

	private static final Object LOCKER = new Object();

	@Autowired
	CacheManager cacheManager;

	@SuppressWarnings("all")
	public <T> T get(String cacheName, Object key) {
		Element element = getOrAddCache(cacheName).get(key);
		if (element == null) {
			return null;
		} else {
			Object objectValue = element.getObjectValue();
			return (T) objectValue;
		}
	}

	/**
	 * 设置值
	 * 
	 * @param cacheName
	 * @param key
	 * @param value
	 */
	public void set(String cacheName, Object key, Object value) {
		getOrAddCache(cacheName).put(new Element(key, value));
	}

	/**
	 * 获取所有key值
	 * 
	 * @param cacheName
	 * @return
	 */
	public List getKeys(String cacheName) {

		return getOrAddCache(cacheName).getKeys();
	}

	/**
	 * 判断缓存key是否存在
	 * 
	 * @param cacheName
	 * @param key
	 * @return
	 */
	public boolean hasKey(String cacheName, String key) {
		return getOrAddCache(cacheName).isKeyInCache(key);
	}

	/**
	 * 移除指定key值
	 * 
	 * @param cacheName
	 * @param key
	 */
	public void del(String cacheName, Object key) {
		getOrAddCache(cacheName).remove(key);
	}

	/**
	 * 移除所有
	 * 
	 * @param cacheName
	 */
	public void delAll(String cacheName) {
		getOrAddCache(cacheName).removeAll();
	}

	private Cache getOrAddCache(String cacheName) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache == null) {
			synchronized (LOCKER) {
				cache = cacheManager.getCache(cacheName);
				if (cache == null) {
					cacheManager.addCacheIfAbsent(cacheName);
					cache = cacheManager.getCache(cacheName);
				}
			}
		}
		return cache;
	}
}
