package com.lzb.rock.base.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author lzb
 *
 */
public class UtilReflexObject {

	/**
	 * 单个对象的所有键值
	 * 
	 * @param object
	 *            单个对象
	 * 
	 * @return Map<String, Object> map
	 */
	public static Map<String, Object> getKeyAndValue(Object obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 得到类对象
		Class userCla = (Class) obj.getClass();
		/* 得到类中的所有属性集合 */
		Field[] fs = userCla.getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			Field f = fs[i];
			// 设置些属性是可以访问的
			f.setAccessible(true); 
			Object val = new Object();
			try {
				val = f.get(obj);
				// 得到此属性的值
				map.put(f.getName(), val);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	/**
	 * 单个对象的某个键的值
	 * 
	 * @param object
	 *            对象
	 * 
	 * @param key
	 *            键
	 * 
	 * @return Object 键在对象中所对应得值 没有查到时返回空字符串
	 */
	public static Object getValueByKey(Object obj, String key) {
		// 得到类对象
		Class userCla = (Class) obj.getClass();
		/* 得到类中的所有属性集合 */
		Field[] fs = userCla.getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			Field f = fs[i];
			// 设置些属性是可以访问的
			f.setAccessible(true); 
			try {

				if (f.getName().endsWith(key)) {
					System.out.println("单个对象的某个键的值==反射==" + f.get(obj));
					return f.get(obj);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		// 没有查到时返回空字符串
		return "";
	}

	/**
	 * 多个（列表）对象的所有键值
	 * 
	 * @param object
	 * @return List<Map<String,Object>> 列表中所有对象的所有键值
	 */
	public static List<Map<String, Object>> getKeysAndValues(List<Object> object) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Object obj : object) {
			Class userCla;
			// 得到类对象
			userCla = (Class) obj.getClass();
			/* 得到类中的所有属性集合 */
			Field[] fs = userCla.getDeclaredFields();
			Map<String, Object> listChild = new HashMap<String, Object>();
			for (int i = 0; i < fs.length; i++) {
				Field f = fs[i];
				// 设置些属性是可以访问的
				f.setAccessible(true); 
				Object val = new Object();
				try {
					val = f.get(obj);
					// 得到此属性的值
					listChild.put(f.getName(), val);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			//将map加入到list集合中
			list.add(listChild);
		}
		System.out.println("多个（列表）对象的所有键值====" + list.toString());
		return list;
	}

	/**
	 * 多个（列表）对象的某个键的值
	 * 
	 * @param object
	 * @param key
	 * @return List<Object> 键在列表中对应的所有值 ex:key为上面方法中的mc字段 那么返回的数据就是
	 */
	public static List<Object> getValuesByKey(List<Object> object, String key) {
		List<Object> list = new ArrayList<Object>();
		for (Object obj : object) {
			// 得到类对象
			Class userCla = (Class) obj.getClass();
			/* 得到类中的所有属性集合 */
			Field[] fs = userCla.getDeclaredFields();
			for (int i = 0; i < fs.length; i++) {
				Field f = fs[i];
				// 设置些属性是可以访问的
				f.setAccessible(true); 
				try {
					if (f.getName().endsWith(key)) {
						list.add(f.get(obj));
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("多个（列表）对象的某个键的值列表====" + list.toString());
		return list;
	}
}