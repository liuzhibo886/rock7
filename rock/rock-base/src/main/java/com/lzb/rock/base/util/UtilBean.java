package com.lzb.rock.base.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import com.alibaba.fastjson.JSONObject;
import com.lzb.rock.base.enums.ResultEnum;
import com.lzb.rock.base.exception.BusException;

/**
 * 对象复制 根据对象属性名称复制
 * 
 * @author lzb 2018年2月8日 下午5:09:03
 */
public class UtilBean extends BeanUtils {
	/**
	 * 
	 * @param <T>
	 * @param clazz
	 * @param orig  原始对象
	 * @return
	 */
	public static <T> T copyProperties(Class<? extends T> clazz, final Object orig) {
		if (orig == null) {
			return null;
		}

		T dest = null;
		try {
			dest = (T) clazz.newInstance();
			BeanUtils.copyProperties(dest, orig);
		} catch (Exception e) {
			throw new BusException(ResultEnum.RUNTIME_ERR, e.getMessage());
		}
		return dest;
	}

	public static <T> List<T> copyListProperties(Class<T> clazz, List<? extends Object> rows) {
		if (rows == null) {
			return null;
		}
		if (rows.size() == 0) {
			return new ArrayList<T>();
		}
		ArrayList<T> newRows = new ArrayList<T>(rows.size());
		for (Object orig : rows) {
			newRows.add(copyProperties(clazz, orig));
		}

		return newRows;
	}

	/**
	 * 
	 * @param dest 新对象
	 * @param orig 原始对象
	 */
	public static void copyProperties(final Object dest, final Object orig) {
		try {
			BeanUtils.copyProperties(dest, orig);

		} catch (Exception e) {
			throw new BusException(ResultEnum.RUNTIME_ERR, e.getMessage());
		}
	}

	/**
	 * 合并对象，目标对象有值，则不会覆盖，只能复制对象
	 * 
	 * @param dest        目标对象
	 * @param orig        源对象
	 * @param targetClass 返回值泛型
	 * @return
	 */
	public static <T> T mergeJavaObject(final Object dest, final Object orig, Class<T> destClazz) {
		JSONObject targetjSON = new JSONObject();
		JSONObject sourceJson = new JSONObject();
		if (dest != null) {
			targetjSON = (JSONObject) JSONObject.toJSON(dest);
		}
		if (orig != null) {
			sourceJson = (JSONObject) JSONObject.toJSON(orig);
		}
		Set<String> targetSet = targetjSON.keySet();
		for (String key : sourceJson.keySet()) {
			if (!targetSet.contains(key)) {
				targetjSON.put(key, sourceJson.get(key));
			} else {
				Object obj = targetjSON.get(key);
				// null覆盖 String 类型空字符串也覆盖
				if (obj == null) {
					targetjSON.put(key, sourceJson.get(key));
				} else if (obj instanceof String) {
					if (StringUtils.isBlank(obj.toString())) {
						targetjSON.put(key, sourceJson.get(key));
					}
				}

			}
		}
		return targetjSON.toJavaObject(destClazz);
	}

	/**
	 * 对象组中是否存在 Empty Object
	 *
	 * @param os 对象组
	 * @return
	 */
	public static boolean isOneEmpty(Object... os) {
		for (Object o : os) {
			if (isEmpty(o)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 对象是否为空
	 *
	 * @param obj String,List,Map,Object[],int[],long[]
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		}
		if (o instanceof String) {
			if ("".equals(o.toString().trim())) {
				return true;
			}
		} else if (o instanceof List) {
			if (((List) o).size() == 0) {
				return true;
			}
		} else if (o instanceof Map) {
			if (((Map) o).size() == 0) {
				return true;
			}
		} else if (o instanceof Set) {
			if (((Set) o).size() == 0) {
				return true;
			}
		} else if (o instanceof Object[]) {
			if (((Object[]) o).length == 0) {
				return true;
			}
		} else if (o instanceof int[]) {
			if (((int[]) o).length == 0) {
				return true;
			}
		} else if (o instanceof long[]) {
			if (((long[]) o).length == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 对象中String空字符串转换null，以免数据库更新 泛型不可更新
	 * 
	 * @param obj
	 * @param targetClass
	 * @return
	 */
	public static void setNull(Object obj) {
		if (obj == null) {
			return;
		}

		Class<? extends Object> clazz = obj.getClass();
		// 获取实体类的所有属性，返回Field数组
		Field[] fields = UtilClass.getDeclaredFields(clazz);
		for (Field field : fields) {
			// 可访问私有变量
			field.setAccessible(true);
			// 获取属性类型
			String type = field.getGenericType().getTypeName();
			// 如果type是类类型，则前面包含"class "，后面跟类名
			if ("class java.lang.String".equals(type)) {
				// 将属性的首字母大写
				try {
					// 调用getter方法获取属性值
					String str = UtilClass.getFieldValue(field.getName(), obj);
					if (StringUtils.isBlank(str)) {
						UtilClass.setFieldValue(field.getName(), null, obj);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将Object对象里面的属性和值转化成Map对象
	 *
	 * @param obj
	 * @return
	 * @throws IllegalAccessException
	 */
	public static Map<String, Object> objectToMap(Object obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		Class<?> clazz = obj.getClass();
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			String fieldName = field.getName();
			try {
				Object value = field.get(obj);
				if (value != null && value instanceof ObjectId) {
					map.put(fieldName, value.toString());
				} else {
					map.put(fieldName, value);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return map;
	}

	public static Object[] objectToArray(Object obj) {
		Class<?> clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		List<Object> list = new ArrayList<Object>(fields.length * 2);

		for (Field field : fields) {
			field.setAccessible(true);
			String fieldName = field.getName();
			try {
				Object value = field.get(obj);
				list.add(fieldName);
				if (value != null && value instanceof ObjectId) {
					list.add(value.toString());
				} else {
					list.add(value);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return list.toArray();
	}
}
