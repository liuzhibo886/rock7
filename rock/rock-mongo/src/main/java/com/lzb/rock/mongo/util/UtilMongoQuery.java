package com.lzb.rock.mongo.util;

import java.lang.reflect.Field;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.lzb.rock.base.util.UtilString;

/**
 * mongodb 构建update对象
 * 
 * @author lzb
 * @date 2020年8月6日下午6:48:18
 */
public class UtilMongoQuery {

	/**
	 * @date 2020年8月17日上午10:46:00
	 * @param obj
	 * @param excludeField
	 * @return
	 */
	public static Query is(Object obj) {

		Criteria criteria = null;
		Class<?> clazz = obj.getClass();
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			String fieldName = field.getName();
			try {
				Object value = field.get(obj);
				if (value == null) {
					continue;
				}
				if (value instanceof String && UtilString.isBlank(value.toString())) {
					continue;
				}

				if (criteria == null) {
					criteria = Criteria.where(fieldName).is(value);
					continue;
				}
				criteria.and(fieldName).is(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Query query = new Query(criteria);
		return query;
	}
}
