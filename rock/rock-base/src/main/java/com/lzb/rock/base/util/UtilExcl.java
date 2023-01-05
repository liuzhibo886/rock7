package com.lzb.rock.base.util;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lzb.rock.base.enums.ResultEnum;
import com.lzb.rock.base.exception.BusException;

/**
 * excl 帮助类
 * 
 * @author lzb
 *
 */
public class UtilExcl {
	/**
	 * 获取当前传输流
	 * 
	 * @param fileName
	 * @return
	 */
	public static OutputStream getOutputStream(String fileName) {
		HttpServletResponse response = UtilHttpKit.getResponse();
		HttpServletRequest request = UtilHttpKit.getRequest();
		return getOutputStream(fileName, response, request);
	}

	/**
	 * 获取当前传输流
	 * 
	 * @param fileName 文件名
	 * @param response
	 * @param request
	 * @return
	 */
	public static OutputStream getOutputStream(String fileName, HttpServletResponse response,
			HttpServletRequest request) {
		String agent = request.getHeader("USER-AGENT").toLowerCase();
		OutputStream ops = null;
		try {
			if (agent.contains("firefox") || agent.contains("chrome")) {
				response.setHeader("Content-disposition",
						"attachment; filename=" + "\"" + new String(fileName.getBytes("utf-8"), "iso8859-1") + "\"");
			} else {
				String codedFileName = java.net.URLEncoder.encode(fileName, "UTF-8");
				response.setHeader("content-disposition", "attachment;filename=" + codedFileName);
			}
			ops = response.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusException(ResultEnum.SYSTTEM_ERR, e.getMessage());
		}

		return ops;
	}
}
