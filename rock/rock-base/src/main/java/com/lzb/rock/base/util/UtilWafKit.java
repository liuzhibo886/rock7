/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzb.rock.base.util;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Web防火墙工具类
 * <p>
 * 
 * @author hubin
 * @Date 2014-5-8
 */
public class UtilWafKit {

	private final static Pattern SCRIPT_PATT = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
	private final static Pattern SCRIPT2_PATT = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
	private final static Pattern SCRIPT3_PATT = Pattern.compile("<script(.*?)>",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	private final static Pattern EVAL_PATT = Pattern.compile("eval\\((.*?)\\)",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	private final static Pattern EXPRESSION_PATT = Pattern.compile("expression\\((.*?)\\)",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	private final static Pattern JAVSCRIPT_PATT = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
	private final static Pattern VBSCRIPT_PATT = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
	private final static Pattern ONLOAD_PATT = Pattern.compile("onload(.*?)=",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

	/**
	 * @Description 过滤XSS脚本内容
	 * @param value 待处理内容
	 * @return
	 */
	public static String stripXss(String rlt) {

		if (StringUtils.isNotBlank(rlt)) {
			// NOTE: It's highly recommended to use the ESAPI library and uncomment the
			// following line to
			// avoid encoded attacks.
			// value = ESAPI.encoder().canonicalize(value);

			// Avoid null characters
			// rlt = value.replaceAll("", "");

			// Avoid anything between script tags
			rlt = SCRIPT_PATT.matcher(rlt).replaceAll("");

			// Avoid anything in a src='...' type of expression
			/*
			 * scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'",
			 * Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL); rlt =
			 * scriptPattern.matcher(rlt).replaceAll("");
			 * 
			 * scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"",
			 * Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL); rlt =
			 * scriptPattern.matcher(rlt).replaceAll("");
			 */

			// Remove any lonesome </script> tag
			rlt = SCRIPT2_PATT.matcher(rlt).replaceAll("");

			// Remove any lonesome <script ...> tag
			rlt = SCRIPT3_PATT.matcher(rlt).replaceAll("");

			// Avoid eval(...) expressions
			rlt = EVAL_PATT.matcher(rlt).replaceAll("");

			// Avoid expression(...) expressions

			rlt = EXPRESSION_PATT.matcher(rlt).replaceAll("");

			// Avoid javascript:... expressions
			rlt = JAVSCRIPT_PATT.matcher(rlt).replaceAll("");

			// Avoid vbscript:... expressions
			rlt = VBSCRIPT_PATT.matcher(rlt).replaceAll("");

			// Avoid onload= expressions

			rlt = ONLOAD_PATT.matcher(rlt).replaceAll("");
		}

		return rlt;
	}

	/**
	 * @Description 过滤SQL注入内容
	 * @param value 待处理内容
	 * @return
	 */
	public static String stripSqlInjection(String value) {
		return (null == value) ? null : value.replaceAll("('.+--)|(--)|(%7C)", "");
	}

	/**
	 * @Description 过滤SQL/XSS注入内容
	 * @param value 待处理内容
	 * @return
	 */
	public static String stripSqlXss(String value) {
		return stripXss(stripSqlInjection(value));
	}

}
