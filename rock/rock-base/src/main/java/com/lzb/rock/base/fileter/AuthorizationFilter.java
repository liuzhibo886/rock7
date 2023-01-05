package com.lzb.rock.base.fileter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.springframework.core.annotation.Order;

import com.lzb.rock.base.holder.ThreadLocalHolder;
import com.lzb.rock.base.util.UtilHttpKit;

import lombok.extern.slf4j.Slf4j;

@Order(2)
@WebFilter(filterName = "AuthorizationFilter", urlPatterns = "/*")
@Slf4j
public class AuthorizationFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String authorization = UtilHttpKit.getHeader("Authorization");

		ThreadLocalHolder.authorizationLocal.set(authorization);
		chain.doFilter(request, response);
		ThreadLocalHolder.authorizationLocal.remove();
	}
}