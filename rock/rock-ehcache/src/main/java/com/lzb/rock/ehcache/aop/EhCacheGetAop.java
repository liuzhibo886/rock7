package com.lzb.rock.ehcache.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.lzb.rock.base.enums.ResultEnum;
import com.lzb.rock.base.exception.BusException;
import com.lzb.rock.base.util.UtilCacheKey;
import com.lzb.rock.base.util.UtilClass;
import com.lzb.rock.base.util.UtilJson;
import com.lzb.rock.base.util.UtilString;
import com.lzb.rock.ehcache.aop.annotation.EhcacheGet;
import com.lzb.rock.ehcache.mapper.EhCacheMapper;

import ch.qos.logback.classic.pattern.Util;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
@Component
@Order(2)
@Slf4j
public class EhCacheGetAop {

	@Autowired
	EhCacheMapper ehCacheMapper;

	@Pointcut(value = "@annotation(com.lzb.rock.ehcache.aop.annotation.EhcacheGet)")
	public void ehCacheGet() {
	}

	@Around("ehCacheGet()")
	private Object handle(ProceedingJoinPoint point) throws Throwable {
		Signature sig = point.getSignature();
		MethodSignature msig = null;
		if (!(sig instanceof MethodSignature)) {
			throw new BusException(ResultEnum.AOP_ERR, "该注解只能用于方法");
		}
		// 类完整路径
		String classType = point.getTarget().getClass().getName();
		msig = (MethodSignature) sig;
		Object target = point.getTarget();
		Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

		EhcacheGet ehcacheGet = currentMethod.getAnnotation(EhcacheGet.class);
		String constant = ehcacheGet.constant();
		String[] parameters = ehcacheGet.parameters();
//		long timeout = ehcacheGet.timeout();
		boolean rest = ehcacheGet.rest();
		String cacheName = ehcacheGet.cacheName();
		// 获取参数列表
		// 方法名
//		String methodName = currentMethod.getName();
//		String className = point.getTarget().getClass().getName();
		Object[] params = point.getArgs();
		String[] names = ((CodeSignature) point.getSignature()).getParameterNames();

		String key = UtilCacheKey.getKey(constant, names, params, parameters);
		Object rs = null;
		boolean flag = ehCacheMapper.hasKey(cacheName, key);

		// 执行方法
		if (!flag || rest) {
			rs = point.proceed();
			if (rs != null && UtilString.isNotBlank(rs.toString())) {
				ehCacheMapper.set(cacheName, key, rs);
				log.debug("刷新缓存cacheName:{},key：{},flag:{}", cacheName, key, flag);
			}
		} else {
			Type returnType = currentMethod.getGenericReturnType();
			rs = ehCacheMapper.get(cacheName, key);
			// rs=UtilClass.getJavaBeanByString(str, returnType);
			log.debug("读取缓存,key：{},value={}", key, UtilJson.getStr(rs));
		}
		return rs;
	}
}
