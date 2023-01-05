package com.lzb.rock.login.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * shiro 啥也不拦截，直接放行
 * 
 * @author lzb
 *
 */
@Slf4j
public class RockFilter implements Filter {

	FilterConfig filterConfig = null;

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		// HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		// log.debug("RockFilter,url:{}", httpServletRequest.getRequestURI());
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		this.filterConfig = null;

	}

}
