package com.lzb.rock.base.util;

import java.io.File;
import java.io.IOException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class UtilGit {

	public static void main(String[] args) throws IOException {
		String path = "G:\\Git.txt";
		String jsonStr = UtilFile.readFileToString(new File(path), "utf-8");
		JSONArray jsonArr = UtilJson.getJsonArray(jsonStr);
		
		String format="git clone %s /mnt/sdb/code/zmsys/code/%s";
		for (Object object : jsonArr) {
			JSONObject obj=(JSONObject) object;
			String path3 = obj.getString("path");
			String http_url_to_repo = obj.getString("http_url_to_repo");
			String gitUrl = String.format(format, http_url_to_repo,path3);
			System.out.println(gitUrl);
		}

	}

}
