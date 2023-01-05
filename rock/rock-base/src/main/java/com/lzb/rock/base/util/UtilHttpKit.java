package com.lzb.rock.base.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestAttributes;

/**
 * Copyright (c) 2015-2016, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.lzb.rock.base.xss.XssHttpServletRequestWrapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author lzb
 * @Date 2019年7月31日 下午4:58:12
 */
@Slf4j
public class UtilHttpKit {
	/**
	 * 获取访问IP
	 * 
	 * @return
	 */
	public static String getIp() {
		HttpServletRequest request = UtilHttpKit.getRequest();
		if (request != null) {
			return request.getRemoteHost();
		}
		return "";
	}

	/**
	 * 获取请求头
	 * 
	 * @param name
	 * @return
	 */
	public static String getHeader(String name) {
		HttpServletRequest request = UtilHttpKit.getRequest();
		if (request != null) {
			return request.getHeader(name);
		}
		return "";
	}

	/**
	 * 获取请求相对地址
	 * 
	 * @param name
	 * @return
	 */
	public static String getRequestUri() {
		HttpServletRequest request = UtilHttpKit.getRequest();
		if (request != null) {
			return request.getRequestURI();
		}
		return "";
	}

	/**
	 * 获取所有请求的值
	 */
	public static Map<String, String> getRequestParameters() {
		HashMap<String, String> values = new HashMap<>();
		HttpServletRequest request = UtilHttpKit.getRequest();
		if (request != null) {
			Enumeration<String> enums = request.getParameterNames();
			while (enums.hasMoreElements()) {
				String paramName = (String) enums.nextElement();
				String paramValue = request.getParameter(paramName);
				values.put(paramName, paramValue);
			}
		}
		return values;
	}

	/**
	 * 获取 HttpServletResponse
	 */
	public static HttpServletResponse getResponse() {
		HttpServletResponse response = null;
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

		if (requestAttributes != null && requestAttributes instanceof ServletRequestAttributes) {
			response = ((ServletRequestAttributes) requestAttributes).getResponse();
		}
		return response;
	}

	/**
	 * 获取 包装防Xss Sql注入的 HttpServletRequest
	 * 
	 * @return request
	 */
	public static HttpServletRequest getRequest() {
		HttpServletRequest request = null;
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes != null && requestAttributes instanceof ServletRequestAttributes) {
			request = ((ServletRequestAttributes) requestAttributes).getRequest();
			request = new XssHttpServletRequestWrapper(request);
		}
		return request;
	}

	/**
	 * 获取外网IP
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr() {
		HttpServletRequest request = UtilHttpKit.getRequest();
		if (request == null) {
			return "";
		}
		String ipAddress = null;
		ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (ipAddress.equals("127.0.0.1")) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ipAddress = inet.getHostAddress();
			}
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
			// = 15
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		return ipAddress;
	}
}
