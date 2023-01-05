package com.lzb.rock.base.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下载nexus3 所有jar
 * 
 * @author liuzhibo
 *
 */
public class DownloadMavenJar {
	private static final String initPath = "G:\\nexusd\\";
	private static final String URL = "http://192.88.2.201:8082/service/rest/v1/components?repository=bot-snapshots";
	private static final String APP_KEY = "admin";
	private static final String SECRET_KEY = "LzTAnPVzSMczH2w2";

	public HashMap<String, Object> readJson() throws IOException {
		// 设置请求头
		HashMap<String, String> header = new HashMap<>();
		// 认证token
		header.put("Authorization", getHeader());

		String jsonStr = UtilHttp.doGet(URL, null, "utf-8", false, null, header);

		return (HashMap<String, Object>) UtilJson.getJavaBean(jsonStr, Map.class);

	}

	public void download() throws IOException {
		HashMap<String, Object> stringStringHashMap = readJson();

		List<Map> list = (List) stringStringHashMap.get("items");

		for (Map map : list) {
			List<Map> assets = (List<Map>) map.get("assets");
			for (Map dMap : assets) {
				String url = String.valueOf(dMap.get("downloadUrl"));
				String path = String.valueOf(dMap.get("path"));
				genFile(url, initPath + path);
			}
		}

	}

	private void genFile(final String urlStr, final String path) {
		System.out.println(path);

		URL url = null;
		try {
			url = new URL(urlStr);
			String tempFileName = path;

			// 先创建文件夹
			File t = new File(path);
			t.getParentFile().mkdirs();
			File temp = new File(tempFileName);
			FileUtils.copyURLToFile(url, temp);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String getHeader() {
		String auth = APP_KEY + ":" + SECRET_KEY;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
		return "Basic " + new String(encodedAuth);
	}

	public static void main(String args[]) throws IOException {
		new DownloadMavenJar().download();
	}
}
