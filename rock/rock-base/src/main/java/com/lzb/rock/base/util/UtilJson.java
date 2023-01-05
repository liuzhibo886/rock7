package com.lzb.rock.base.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lzb.rock.base.enums.ResultEnum;
import com.lzb.rock.base.exception.BusException;

/**
 * json转换帮助类
 * 
 * @author SEELE
 *
 */
public class UtilJson {

	/**
	 * 判断是不是json字符串
	 * 
	 * @param str
	 * @return
	 */
	public static Boolean isJsonString(String str) {
		if ((str.startsWith("{") && str.endsWith("}")) || (str.startsWith("[") && str.endsWith("]"))) {
			return true;
		}
		return false;
	}

	/**
	 * json 字符串转换成JSONObject
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static JSONObject getJsonObject(String jsonStr) {
		Object obj = JSON.parse(jsonStr);
		if (obj instanceof JSONArray) {
			throw new BusException(ResultEnum.JSON_ERR, "JSON字符串转换为JSONObject类型失败,原字符串为JSONArray类型");
		}
		return (JSONObject) obj;
	}

	/**
	 * JSON 字符串转换java对象
	 * 
	 * @param jsonStr
	 * @param clazz
	 * @return
	 */
	public static <T> T getJavaBean(String jsonStr, Class<T> clazz) {
		if (StringUtils.isBlank(jsonStr)) {
			return null;
		}
		return JSONObject.parseObject(jsonStr, clazz);
	}

	public static <T> T getJavaBean(String jsonStr, TypeReference<T> typeReference) {
		if (StringUtils.isBlank(jsonStr)) {
			return null;
		}
		return JSONObject.parseObject(jsonStr, typeReference);
	}

	/**
	 * JSON 字符串转换java对象
	 * 
	 * @param jsonStr
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> getJavaList(String jsonStr, Class<T> clazz) {
		if (StringUtils.isBlank(jsonStr)) {
			return null;
		}
		return JSONObject.parseArray(jsonStr, clazz);
	}

	/**
	 * json 字符串转换成JSONArray
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static JSONArray getJsonArray(String jsonStr) {
		Object obj = JSON.parse(jsonStr);
		if (obj instanceof JSONObject) {
			throw new BusException(ResultEnum.JSON_ERR, "JSON字符串转换为JSONArray类型失败,原字符串为JSONObject类型");
		}
		return (JSONArray) obj;
	}

	/**
	 * java对象转换为json字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String getStr(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof String) {
			return obj.toString();
		}
		return JSONObject.toJSONString(obj);
	}

	/**
	 * 转换成json 字符串，并格式化
	 * 
	 * @param obj
	 * @return
	 */
	public static String getStrByFormat(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof String) {
			return obj.toString();
		}
		return JSONObject.toJSONString(obj,SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, 
	            SerializerFeature.WriteDateUseDateFormat);
	}

}
