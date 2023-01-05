package com.lzb.rock.mongo.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.mongodb.core.query.Update;

/**
 * mongodb 构建update对象
 * 
 * @author lzb
 * @date 2020年8月6日下午6:48:18
 */
public class UtilMongoUpdate {

	/**
	 * @date 2020年8月17日上午10:46:00
	 * @param obj
	 * @param excludeField
	 * @return
	 */
	public static Update set(Object obj, String... excludeField) {
		List<String> excludeFieldArr = null;
		if (excludeField != null) {
			excludeFieldArr = Arrays.asList(excludeField);
		}

		Update update = new Update();
		Class<?> clazz = obj.getClass();
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			String fieldName = field.getName();
			if (excludeFieldArr != null && excludeFieldArr.contains(fieldName)) {
				continue;
			}
			try {
				Object value = field.get(obj);
				if (value != null) {
					update.set(fieldName, value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return update;
	}
}
