package com.lzb.rock.base.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.lzb.rock.base.ssl.MySslProtocolSocketFactory;
import com.lzb.rock.base.util.UtilJson;
import com.lzb.rock.base.util.UtilString;

import lombok.extern.slf4j.Slf4j;

/**
 * HTTP工具类 发送http/https协议get/post请求，发送map，json，xml，txt数据
 * 
 * @author happyqing 2016-5-20
 */
@Slf4j
public final class UtilHttp {

	/**
	 * 发送get请求
	 * 
	 * @param url
	 * @return
	 */
	public static String doGet(String url) {

		return doGet(url, null, "utf-8", false, null, null);
	}

	/**
	 * 执行一个http/https get请求，返回请求响应的文本数据
	 * 
	 * @param url     请求的URL地址
	 * @param params  请求的查询参数,key value 或者凭借字符串
	 * @param charset 字符集
	 * @param pretty  是否美化
	 * @return 返回请求响应的文本数据
	 */
	public static String doGet(String url, String params, String charset, boolean pretty, HttpClient client,
			Map<String, String> header) {
		StringBuffer response = new StringBuffer();
		if (client == null) {
			client = new HttpClient();
		}
		if (UtilString.isNotBlank(charset)) {
			charset = "UTF-8";
		}

		if (url.startsWith("https")) {
			// https请求
			Protocol myhttps = new Protocol("https", new MySslProtocolSocketFactory(), 443);
			Protocol.registerProtocol("https", myhttps);
		}
		HttpMethod method = new GetMethod(url);
		if (header != null) {
			for (Entry<String, String> entry : header.entrySet()) {
				method.addRequestHeader(entry.getKey(), entry.getValue());
			}
		}

		client.setConnectionTimeout(3000);
		client.setTimeout(3000);
		try {
			if (StringUtils.isNotBlank(params)) {
				// 判断是否拼接字符串
				if (params.indexOf("=") > -1 || params.indexOf("&") > -1) {
					/**
					 * 对get请求参数编码，汉字编码后，就成为%式样的字符串
					 */
					method.setQueryString(URIUtil.encodeQuery(params));
				}
				if (UtilJson.isJsonString(params)) {
					JSONObject obj = UtilJson.getJsonObject(params);
					List<NameValuePair> nameValuePairs = new ArrayList<>();
					for (Entry<String, Object> entry : obj.entrySet()) {
						String key = entry.getKey();
						Object value = entry.getValue();
						if (UtilString.isNotBlank(key) && value != null) {
							NameValuePair nameValuePair = new NameValuePair();
							nameValuePair.setName(key);
							nameValuePair.setValue(value.toString());
							nameValuePairs.add(nameValuePair);
						}
					}
					NameValuePair[] query = new NameValuePair[nameValuePairs.size()];
					nameValuePairs.toArray(query);
					method.setQueryString(query);
				}
			}

			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(), charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty) {
						response.append(line).append(System.getProperty("line.separator"));
					} else {
						response.append(line);
					}
				}
				reader.close();
			} else {
				log.error("执行Get请求时异常，请求码：{}", method.getStatusCode());
			}
		} catch (Exception e) {
			log.error("执行Get请求时，参数“" + params + "”发生异常！", e);
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

	/**
	 * post 请求 param 传参数
	 * 
	 * @param url     请求地址
	 * @param params  key value 数据
	 * @param charset 编码
	 * @param pretty  是否优化
	 * @return
	 */
	public static String postParam(String url, String params, String charset, boolean pretty, HttpClient client,
			Map<String, String> header) {
		StringBuffer response = new StringBuffer();
		if (client == null) {
			client = new HttpClient();
		}
		if (UtilString.isBlank(charset)) {
			charset = "UTF-8";
		}
		if (url.startsWith("https")) {
			// https请求
			Protocol myhttps = new Protocol("https", new MySslProtocolSocketFactory(), 443);
			Protocol.registerProtocol("https", myhttps);
		}
		PostMethod method = new PostMethod(url);
		
		if (header != null) {
			for (Entry<String, String> entry : header.entrySet()) {
				method.addRequestHeader(entry.getKey(), entry.getValue());
			}
		}
		// 设置参数的字符集
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
		// 设置post数据
		if (params != null) {
			JSONObject obj = UtilJson.getJsonObject(params);
			for (Entry<String, Object> entry : obj.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if (UtilString.isNotBlank(key) && value != null) {
					method.setParameter(key, value.toString());
				}
			}
		}
		try {
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(), charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty) {
						response.append(line).append(System.getProperty("line.separator"));
					} else {
						response.append(line);
					}
				}
				reader.close();
			} else {
				log.error("请求失败，错误码:", method.getStatusCode());
			}
		} catch (IOException e) {
			log.error("执行Post请求" + url + "时，发生异常！", e);
		} finally {
			method.releaseConnection();
		}
		return response.toString();

	}

	/**
	 * 
	 * @date 2020年7月30日上午11:18:51
	 * @param url
	 * @param content
	 * @return
	 */
	public static String postBody(String url, String content) {

		return postBody(url, content, "utf-8", true, null, null);
	}

	/**
	 * 执行一个http/https post请求， 直接写数据 json,xml,txt
	 * 
	 * @param url     请求的URL地址
	 * @param params  请求的查询参数,可以为null
	 * @param charset 字符集
	 * @param pretty  是否美化
	 * @return 返回请求响应的文本数据
	 */
	public static String postBody(String url, String content, String charset, boolean pretty, HttpClient client,
			Map<String, String> header) {
		StringBuffer response = new StringBuffer();
		if (client == null) {
			client = new HttpClient();
		}
		if (UtilString.isBlank(charset)) {
			charset = "UTF-8";
		}

		if (url.startsWith("https")) {
			// https请求
			Protocol myhttps = new Protocol("https", new MySslProtocolSocketFactory(), 443);
			Protocol.registerProtocol("https", myhttps);
		}

		PostMethod method = new PostMethod(url);

		if (header != null) {
			for (Entry<String, String> entry : header.entrySet()) {
				method.addRequestHeader(entry.getKey(), entry.getValue());
			}
		}
		try {
			// 设置请求头部类型参数
			// method.setRequestHeader("Content-Type","text/plain;
			// charset=utf-8");//application/json,text/xml,text/plain
			// method.setRequestBody(content); //InputStream,NameValuePair[],String
			// RequestEntity是个接口，有很多实现类，发送不同类型的数据；application/json,text/xml,text/plain
			if (UtilString.isNotBlank(content)) {
				RequestEntity requestEntity = new StringRequestEntity(content, "application/json", charset);
				method.setRequestEntity(requestEntity);
			}
			client.executeMethod(method);
			if (method.getStatusCode() != HttpStatus.SC_OK) {
				log.error("请求失败，错误码:", method.getStatusCode());
			}
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(method.getResponseBodyAsStream(), charset));
			String line;
			while ((line = reader.readLine()) != null) {
				if (pretty) {
					response.append(line).append(System.getProperty("line.separator"));
				} else {
					response.append(line);
				}
			}
			reader.close();

		} catch (Exception e) {
			log.error("执行Post请求" + url + "时，发生异常！", e);
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

	
	public static String putBody(String url, String content, String charset, boolean pretty, HttpClient client,
			Map<String, String> header) {
		StringBuffer response = new StringBuffer();
		if (client == null) {
			client = new HttpClient();
		}
		if (UtilString.isBlank(charset)) {
			charset = "UTF-8";
		}

		if (url.startsWith("https")) {
			// https请求
			Protocol myhttps = new Protocol("https", new MySslProtocolSocketFactory(), 443);
			Protocol.registerProtocol("https", myhttps);
		}

		PutMethod method = new PutMethod(url);

		if (header != null) {
			for (Entry<String, String> entry : header.entrySet()) {
				method.addRequestHeader(entry.getKey(), entry.getValue());
			}
		}
		try {
			// 设置请求头部类型参数
			// method.setRequestHeader("Content-Type","text/plain;
			// charset=utf-8");//application/json,text/xml,text/plain
			// method.setRequestBody(content); //InputStream,NameValuePair[],String
			// RequestEntity是个接口，有很多实现类，发送不同类型的数据；application/json,text/xml,text/plain
			if (UtilString.isNotBlank(content)) {
				RequestEntity requestEntity = new StringRequestEntity(content, "application/json", charset);
				method.setRequestEntity(requestEntity);
			}
			client.executeMethod(method);
			if (method.getStatusCode() != HttpStatus.SC_OK) {
				log.error("请求失败，错误码:", method.getStatusCode());
			}
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(method.getResponseBodyAsStream(), charset));
			String line;
			while ((line = reader.readLine()) != null) {
				if (pretty) {
					response.append(line).append(System.getProperty("line.separator"));
				} else {
					response.append(line);
				}
			}
			reader.close();

		} catch (Exception e) {
			log.error("执行Post请求" + url + "时，发生异常！", e);
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

	/**
	 * 返回二进制数据
	 * 
	 * @param url
	 * @param content
	 * @param charset
	 * @return
	 */
	public static byte[] writePost(String url, String content, String charset, String contentType) {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		if (url.startsWith("https")) {
			// https请求
			Protocol myhttps = new Protocol("https", new MySslProtocolSocketFactory(), 443);
			Protocol.registerProtocol("https", myhttps);
		}
		PostMethod method = new PostMethod(url);
		byte[] b = null;
		try {
			// 设置请求头部类型参数
			// method.setRequestHeader("Content-Type","text/plain;
			// charset=utf-8");//application/json,text/xml,text/plain
			// method.setRequestBody(content); //InputStream,NameValuePair[],String
			// RequestEntity是个接口，有很多实现类，发送不同类型的数据
			if (UtilString.isBlank(contentType)) {
				contentType = "text/xml";
			}
			// application/json,text/xml,text/plain
			RequestEntity requestEntity = new StringRequestEntity(content, contentType, charset);
			method.setRequestEntity(requestEntity);
			client.executeMethod(method);

			if (method.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream bytestream = new ByteArrayOutputStream();

				// IOUtils.readFully(input, length)
				InputStream in = method.getResponseBodyAsStream();
				Integer ch = 0;
				while ((ch = in.read()) != -1) {
					bytestream.write(ch);
				}
				b = bytestream.toByteArray();
				bytestream.close();
				in.close();
			}
		} catch (Exception e) {
			log.error("执行Post请求" + url + "时，发生异常！", e);
		} finally {
			method.releaseConnection();
		}
		return b;
	}

	/**
	 * 远程服务器下载文件
	 * 
	 * @param fileUrl服务器文件地址
	 * @param fileName       保存文件名
	 * @param filePath       保存路径
	 * @throws Exception
	 */
	public static void downloadFile(String fileUrl, String fileName, String filePath) throws Exception {
		// 创建一个URL链接
		URL url = new URL(fileUrl);
		// 获取连接
		URLConnection conn = url.openConnection();
		// 连接超时
		conn.setConnectTimeout(1000 * 30);
		// 读取超时
		conn.setReadTimeout(1000 * 30);
		if (StringUtils.isBlank(fileName)) {
			// 获取文件全路径
			fileName = url.getFile();
			// 获取文件名
			fileName = fileName.substring(fileName.lastIndexOf("/"));
		}
		if (!filePath.endsWith(File.separatorChar + "")) {
			filePath = filePath + File.separatorChar;
		}
		// 目录不存在创建
		File out = new File(filePath);
		if (!out.exists()) {
			new File(filePath).mkdirs();// 创建目录
		}
		// 文件已经存在,删除，重新下载
		File file = new File(filePath, fileName);
		if (file.exists()) {
			file.delete();
		}
		// 获取文件大小
		Integer fileSize = conn.getContentLength();
		// 获取流
		InputStream in = conn.getInputStream();
		// 保存文件流
		FileOutputStream fos = new FileOutputStream(file, true);
		// 定义缓存大小
		byte[] buffer = new byte[1024];
		while (in.read(buffer) != -1) {
			fos.write(buffer);
		}
		fos.close();
		in.close();
	}
//	public static void main(String[] args) {
//
//		String y = doGet("http://video.sina.com.cn/life/tips.html", null, "GBK", true, null);
//		System.out.println(y);
//		String url = "https://www.baidu.com/baidu?wd=张三&tn=monline_7_dg&ie=utf-8";
//		JSONObject obj = new JSONObject();
//		obj.put("wd", "张三");
//		obj.put("tn", "monline_7_dg");
//		obj.put("ie", "utf-8");
//		y = doGet("http://video.sina.com.cn/life/tips.html", obj.toJSONString(), "GBK", true, null);
//		System.out.println(y);
//	}

}
