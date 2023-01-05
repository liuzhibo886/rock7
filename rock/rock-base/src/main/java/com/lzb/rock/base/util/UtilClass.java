package com.lzb.rock.base.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.lzb.rock.base.enums.ResultEnum;
import com.lzb.rock.base.exception.BusException;

import lombok.extern.slf4j.Slf4j;

/**
 * 反射操作类
 * 
 * @author liuzhibo
 *
 *         2020年1月8日 下午3:20:44
 */
@Slf4j
public class UtilClass {

	/**
	 * 获取对象中所有的Field
	 * 
	 * @author liuzhibo
	 * @date 2020年1月8日 下午3:29:11
	 * @param clazz
	 */
	public static Field[] getDeclaredFields(Class<? extends Object> clazz) {

		Field[] fields = clazz.getDeclaredFields();
		return fields;
	}

	/**
	 * 反射获取对象属性值
	 * 
	 * @author liuzhibo
	 * @date 2019年12月23日 下午6:39:06
	 * @param <T>
	 * @param obj
	 * @return
	 */
	public static <T> T getFieldValue(String fieldName, Object obj) {

		Class<? extends Object> clazz = obj.getClass();
		String methodName = fieldName.replaceFirst(fieldName.substring(0, 1), fieldName.substring(0, 1).toUpperCase());
		T value = null;
		Object objValue = null;
		try {
			Method methodGet = clazz.getMethod("get" + methodName);
			objValue = methodGet.invoke(obj);
		} catch (Exception e) {
			log.info("fieldName：{}不存在", fieldName);
		}

		if (objValue != null) {
			value = (T) objValue;
		}

		return value;
	}

	/**
	 * 反射获取对象的属性值
	 * 
	 * @param fieldName
	 * @param obj
	 * @return
	 */
	public static Object getFieldValueObj(String fieldName, Object obj) {

		Class<? extends Object> clazz = obj.getClass();
		String methodName = fieldName.replaceFirst(fieldName.substring(0, 1), fieldName.substring(0, 1).toUpperCase());
		Object objValue = null;
		try {
			Method methodGet = clazz.getMethod("get" + methodName);
			objValue = methodGet.invoke(obj);
		} catch (Exception e) {
			log.info("fieldName：{}不存在", fieldName);
		}
		return objValue;
	}

	/**
	 * 反射注入对象属性值
	 * 
	 * @author liuzhibo
	 * @date 2019年12月23日 下午6:39:06
	 * @param <T>
	 * @param obj
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public static void setFieldValue(String fieldName, Object fieldValue, Object obj) {
		Class<? extends Object> clazz = obj.getClass();
		Field field;
		try {
			field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(obj, fieldValue);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	/**
	 * 字符串转换对应list
	 * 
	 * @param str
	 * @param type
	 * @return
	 */
	public static <T> List<T> getJavaListByString(String str, Class<T> clazz) {
		boolean isJson = UtilJson.isJsonString(str);
		if (isJson) {
			Object jsonObj = JSON.parse(str);
			if (jsonObj instanceof JSONArray) {
				JSONArray obj = (JSONArray) jsonObj;
				return obj.toJavaList(clazz);
			}
		}
		throw new BusException(ResultEnum.TYPE_ERR, "类型异常,只能转换list JSON 字符串" + clazz.getTypeName());
	}

	public static <T> T getJavaBean(Object obj, Class<T> clazz) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof JSONObject) {
			return ((JSONObject) obj).toJavaObject(clazz);
		}

		if (obj instanceof String) {
			return getJavaBeanByString(obj.toString(), clazz);
		}

		return TypeUtils.castToJavaBean(obj, clazz);
	}

	/**
	 * 字符串转换java对象
	 * 
	 * @param <T>
	 * @param str
	 * @param clazz
	 * @return
	 */
	public static <T> T getJavaBeanByString(String str, Class<T> clazz) {
		if (str == null) {
			return null;
		}
		// 判断是不是json字符串
		boolean isJson = UtilJson.isJsonString(str);
		// JSON字符串
		if (isJson) {
			Object jsonObj = JSON.parse(str);
			if (jsonObj instanceof JSONObject) {
				JSONObject obj = (JSONObject) jsonObj;
				return obj.toJavaObject(clazz);
			} else if (jsonObj instanceof JSONArray) {
//				JSONArray obj = (JSONArray) jsonObj;
//				return (T) obj.toJavaList(clazz);
				throw new BusException(ResultEnum.TYPE_ERR, "类型异常,不能转换list类型;" + clazz.getTypeName());
			}
		}
		// 非JSON字符串
		if (!isJson) {
			String typeName = clazz.getTypeName();
			if (typeName.equals("int") || typeName.equals("java.lang.Integer")) {
				if (StringUtils.isNotBlank(str)) {
					return (T) Integer.valueOf(str);
				} else {
					return null;
				}
			} else if (typeName.equals("java.lang.String")) {
				if (UtilString.isBlank(str) || "NULL".equals(str.trim().toUpperCase())) {
					return null;
				}
				return (T) str;
			} else if (typeName.equals("java.lang.Double") || typeName.equals("double")) {
				if (StringUtils.isNotBlank(str)) {
					return (T) Double.valueOf(str);
				} else {
					return null;
				}
			} else if (typeName.equals("java.lang.Long") || typeName.equals("long")) {
				if (StringUtils.isNotBlank(str)) {
					return (T) Long.valueOf(str);
				} else {
					return null;
				}
			} else if (typeName.equals("java.lang.Short") || typeName.equals("short")) {
				if (StringUtils.isNotBlank(str)) {
					return (T) Short.valueOf(str);
				} else {
					return null;
				}
			} else if (typeName.equals("java.lang.Byte") || typeName.equals("byte")) {
				if (StringUtils.isNotBlank(str)) {
					return (T) Byte.valueOf(str);
				} else {
					return null;
				}
			} else if (typeName.equals("java.lang.Float") || typeName.equals("float")) {
				if (StringUtils.isNotBlank(str)) {
					return (T) Float.valueOf(str);
				} else {
					return null;
				}
			} else if (typeName.equals("java.lang.Boolean") || typeName.equals("boolean")) {
				if (StringUtils.isNotBlank(str)) {
					return (T) Boolean.valueOf(str);
				} else {
					return (T) new Boolean("false");
				}
			} else if (typeName.equals("java.math.BigDecimal")) {
				if (StringUtils.isNotBlank(str)) {
					return (T) new BigDecimal(str);
				} else {
					return null;
				}
			} else {
				throw new BusException(ResultEnum.TYPE_ERR, "类型异常" + typeName);
			}
		}
		throw new BusException(ResultEnum.TYPE_ERR, "类型异常" + clazz.getTypeName());
	}

	/**
	 * 获取目录下包含注解的类
	 * 
	 * @param <A>
	 * 
	 * @param packages
	 * @param annotationClass
	 * @return
	 */
	public static <A extends Annotation> List<Class<?>> getClassByAnnotation(String packages,
			Class<A> annotationClass) {

		List<Class<?>> clazzs = new ArrayList<Class<?>>();

		// spring工具类，可以获取指定路径下的全部类
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		try {
			String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ ClassUtils.convertClassNameToResourcePath(packages);
			if (pattern.endsWith("/")) {
				pattern = pattern + "*.class";
			} else {
				pattern = pattern + "/*.class";
			}

			Resource[] resources = resourcePatternResolver.getResources(pattern);
			// MetadataReader 的工厂类
			MetadataReaderFactory readerfactory = new CachingMetadataReaderFactory(resourcePatternResolver);
			for (Resource resource : resources) {
				// 用于读取类信息
				MetadataReader reader = readerfactory.getMetadataReader(resource);
				// 扫描到的class
				String classname = reader.getClassMetadata().getClassName();
				Class<?> clazz = Class.forName(classname);
				// 判断是否有指定主解
				A anno = clazz.getAnnotation(annotationClass);

				if (anno != null) {
					clazzs.add(clazz);
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			log.error(UtilExceptionStackTrace.getStackTrace(e));
		}

		return clazzs;
	}

	/**
	 * 反射构建对象
	 * 
	 * @param <T>
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public static <T> T newInstance(String className) throws Exception {

		Class<?> clazz = Class.forName(className);

		return (T) clazz.newInstance();
	}

	public static void main(String[] args) {
		List<Class<?>> rows = UtilClass.getClassByAnnotation("com.lzb.rock.**", Documented.class);
		for (Class<?> class1 : rows) {
			System.out.println(class1.getTypeName());
		}
	}
}
