package com.lzb.rock.base.config;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.lzb.rock.base.model.Result;
import com.lzb.rock.base.util.UtilClass;
import com.lzb.rock.base.util.UtilPrometheus;

import io.micrometer.core.instrument.Counter;
import lombok.extern.slf4j.Slf4j;

/**
 * 拦截springboot 返回值
 * 
 * @author liuzhibo
 *
 *         2021年11月29日 上午11:03:08
 */
@ControllerAdvice
@Slf4j
public class BusResponseBodyAdvice implements ResponseBodyAdvice<Object> {

	Counter counterErrr = UtilPrometheus.initCounter("http.server.code.err", "err", "err");

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		if (body == null) {
			return body;
		}

		if (!(body instanceof Result)) {
			return body;
		}

		Object obj = UtilClass.getFieldValueObj("code", body);

		if (obj == null) {
			return body;
		}

		if (!"SUCCESS".equals(obj.toString())) {
			counterErrr.increment();
		}
		return body;
	}
}
