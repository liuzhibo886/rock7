package com.lzb.rock.base.config;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import lombok.extern.slf4j.Slf4j;

/**
 * 类型不匹配时打印访问url
 * 
 * @author lzb
 *
 */
@Configuration
@ConditionalOnMissingClass("org.springframework.cloud.gateway.config.GatewayLoadBalancerClientAutoConfiguration")
@Slf4j
public class BusHandlerExceptionResolverCfg extends DefaultHandlerExceptionResolver {

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		outLog(request);
		return super.doResolveException(request, response, handler, ex);
	}

	/**
	 * 输出日志
	 * 
	 * @param request
	 */
	private void outLog(HttpServletRequest request) {

		String uri = request.getRequestURI();
		log.warn(uri);
		Map<String, String[]> map = request.getParameterMap();
		for (Entry<String, String[]> entry : map.entrySet()) {
			log.warn("{}={}", entry.getKey(), entry.getValue());
		}
	}
}
