package com.lzb.rock.base.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.management.RuntimeErrorException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilLoadProperties {

	private static Logger log = LoggerFactory.getLogger(UtilLoadProperties.class);

	public static Properties init(String path) {
		log.info("初始化配置文件开始，文件名:{}", path);
		Properties prop = null;
		try {
			prop = loadConfigProp(path);

		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("初始化配置文件结束，文件名:{}", path);

		return prop;
	}

	private static Properties loadConfigProp(String filePath) throws IOException {
		Properties prop = getPropertyByFileName(filePath);
		return prop;
	}

	// 加载配置文件
	private static Properties getPropertyByFileName(String fileName) {
		InputStream in = null;
		BufferedReader reader = null;
		Properties preps = new Properties();
		try {
			in = UtilLoadProperties.class.getClassLoader().getResourceAsStream(fileName);
			if (in == null) {
				throw new RuntimeException("配置文件:" + fileName + ",未找到！！");
			}
			reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			preps.load(reader);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage() + "," + fileName + "配置文件找不到");
		} finally {
			if (in != null) {

				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return preps;
	}

	/**
	 * 根据key获取配置文件参数
	 * 
	 * @param key
	 * @return
	 */
	public static String getValue(String key, Properties prop) {

		String value = prop.getProperty(key);
		return null == value ? null : value.trim();
	}

	/**
	 * 获取配置文件中所有配置项
	 * 
	 * @param prop
	 * @return
	 */
	public static Map<String, String> getMapsKeyIsInt(Properties prop) {
		Map<String, String> reMap = new HashMap<String, String>();

		Set<String> set = prop.stringPropertyNames();

		for (String key : set) {
			if (StringUtils.isNoneBlank(key)) {
				String value = prop.getProperty(key);
				reMap.put(key, value);
			} else {
				throw new RuntimeErrorException(null, "key:" + key + ",key is not Integer");
			}
		}
		return reMap;
	}

	/**
	 * 获取配置文件中所有配置项
	 * 
	 * @param prop
	 * @return
	 */
	public static Map<String, String> getMapsKeyIsString(Properties prop) {
		Map<String, String> reMap = new HashMap<String, String>();

		Set<String> set = prop.stringPropertyNames();

		for (String key : set) {
			if (StringUtils.isNotBlank(key)) {
				String value = prop.getProperty(key);
				if (value != null) {
					value = value.trim();
				}
				reMap.put(key, value);
			} else {
				throw new RuntimeErrorException(null, "key is null");
			}
		}
		return reMap;
	}

	public static void main(String[] args) {
		Properties prop = UtilLoadProperties.init("subTreasury.properties");
		Map<String, String> map = UtilLoadProperties.getMapsKeyIsString(prop);
		for (Entry<String, String> entry : map.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}

	}

}
