package com.lzb.rock.base.model;

import org.springframework.http.HttpMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关url对象
 * 
 * @author lzb
 *
 *         2020年12月18日 上午9:01:12
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlPattern {

	String pattern;

	HttpMethod method;
}
