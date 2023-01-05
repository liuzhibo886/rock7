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
import com.lzb.rock.ehcache.aop.annotation.EhcacheDel;
import com.lzb.rock.ehcache.mapper.EhCacheMapper;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

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
public class EhCacheDelAop {

	@Autowired
	EhCacheMapper ehCacheMapper;

	@Pointcut(value = "@annotation(com.lzb.rock.ehcache.aop.annotation.EhcacheDel)")
	public void ehcacheDel() {
	}

	@Around("ehcacheDel()")
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

		EhcacheDel ehcacheDel = currentMethod.getAnnotation(EhcacheDel.class);
		String constant = ehcacheDel.constant();
		String[] parameters = ehcacheDel.parameters();
		String cacheName=ehcacheDel.cacheName();
		// 获取参数列表
		// 方法名
		Object[] params = point.getArgs();
		String[] names = ((CodeSignature) point.getSignature()).getParameterNames();
		String key = UtilCacheKey.getKey(constant, names, params, parameters);
		// 执行方法
		Object rs = point.proceed();
		// 生成缓存key值
		ehCacheMapper.del(cacheName,key);
		log.debug("删除缓存,cacheName:{}key:{}",cacheName, key);
		return rs;
	}
}
