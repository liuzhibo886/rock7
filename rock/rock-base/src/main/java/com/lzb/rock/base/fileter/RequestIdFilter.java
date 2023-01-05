package com.lzb.rock.base.fileter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;

import com.lzb.rock.base.holder.ThreadLocalHolder;
import com.lzb.rock.base.util.UtilHttpKit;
import com.lzb.rock.base.util.UtilString;

import lombok.extern.slf4j.Slf4j;



@Order(Integer.MIN_VALUE)
@WebFilter(filterName = "RequestIdFilter", urlPatterns = "/*")
@Slf4j
public class RequestIdFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String requestId = UtilHttpKit.getHeader("requestId");
		if (UtilString.isBlank(requestId)) {
			requestId = ThreadLocalHolder.requestIdLocal.get();
			if (UtilString.isBlank(requestId)) {
				requestId =UtilString.shortUuid();
			}
		}
		ThreadLocalHolder.requestIdLocal.set(requestId);
		MDC.put("requestId", requestId);
		chain.doFilter(request, response);
		ThreadLocalHolder.requestIdLocal.remove();
	}
	
}