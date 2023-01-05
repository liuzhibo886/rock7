package com.lzb.rock.base.util;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.lzb.rock.base.enums.ResultEnum;
import com.lzb.rock.base.exception.BusException;

/**
 * 缓存内部工具类
 * 
 * @author lzb 2018年7月11日 下午2:53:51
 */
public class UtilCacheKey {

	/**
	 * 生成缓存最终key值
	 * 
	 * @param constant
	 * @param names
	 * @param params
	 * @param parameters
	 * @return
	 */
	public static String getKey(String constant, String[] names, Object[] params, String[] parameters) {

		if (parameters == null || parameters.length < 1) {
			return getKey(constant, params);
		}

		StringBuffer sb = new StringBuffer();
		if (constant.endsWith("_")) {
			sb.append(constant);
		} else {
			sb.append(constant).append("_");
		}

		for (String parameter : parameters) {
			String key1 = getParam(names, params, parameter);
			sb.append(key1).append("_");
		}
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * 没有指定生成key策略时
	 * 
	 * @param constant
	 * @param params
	 * @return
	 */
	public static String getKey(String constant, Object[] params) {
		StringBuffer sb = new StringBuffer();
		if (constant.endsWith("_")) {
			sb.append(constant);
		} else {
			sb.append(constant).append("_");
		}
		for (Object parameter : params) {
			sb.append(parameter.toString()).append("_");
		}
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * 获取缓存key
	 * 
	 * @param names     参数名称数组
	 * @param params    参数值数组
	 * @param parameter 取值的参数名
	 * @return
	 */
	public static String getParam(String[] names, Object[] params, String parameter) {
		String param = null;
		if (StringUtils.isBlank(parameter)) {
			throw new BusException(ResultEnum.AOP_ERR, "parameter 为空");
		}
		if (parameter.endsWith(".")) {
			throw new BusException(ResultEnum.AOP_ERR, "parameter不能以(.) 结尾");
		}

		// 对象中取值
		if (parameter.indexOf(".") > -1) {
			String[] params2 = parameter.split("\\.");
			String param2 = params2[0];
			// 获取params下标
			Integer index = 0;
			for (String name : names) {
				if (name.equals(param2)) {
					break;
				}
				index++;
			}
			// 判断是否下标越界
			if (params.length <= index) {
				throw new BusException(ResultEnum.AOP_ERR, "缓存key错误，检查是否跟属性保持一致");
			}
			// 取值
			Object paramNew = params[index];
			JSONObject json = (JSONObject) JSONObject.toJSON(paramNew);
			int size = params2.length;
			int maxIndex = size - 1;
			for (int i = 1; i < size; i++) {
				String key = params2[i];
				if (i == maxIndex) {
					param = json.getString(key);
				} else {
					json = json.getJSONObject(key);
				}
			}

		} else {
			// 获取params下标
			Integer index = 0;
			for (String name : names) {
				if (name.equals(parameter)) {
					break;
				}
				index++;
			}
			// 判断是否找到
			if (params.length <= index) {
				throw new BusException(ResultEnum.AOP_ERR, "缓存key错误，检查是否跟属性保持一致");
			}
			param = params[index].toString();
		}
		return param;
	}
}
