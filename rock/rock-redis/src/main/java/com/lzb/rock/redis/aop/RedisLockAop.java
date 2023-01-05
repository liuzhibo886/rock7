package com.lzb.rock.redis.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.lzb.rock.base.enums.ResultEnum;
import com.lzb.rock.base.exception.BusException;
import com.lzb.rock.base.util.UtilCacheKey;
import com.lzb.rock.redis.aop.annotation.RedisLock;
import com.lzb.rock.redis.mapper.RedisMapper;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Order(2)
@Slf4j
public class RedisLockAop {

	@Autowired
	RedisMapper redisMapper;

	@Pointcut(value = "@annotation(com.lzb.rock.redis.aop.annotation.RedisLock)")
	public void redisLock() {
	}

	@Around("redisLock()")
	private Object handle(ProceedingJoinPoint point) throws Throwable {
		Signature sig = point.getSignature();
		MethodSignature msig = null;
		if (!(sig instanceof MethodSignature)) {
			throw new BusException(ResultEnum.AOP_ERR, "该注解只能用于方法");
		}
		// 类完整路径
		// String classType = point.getTarget().getClass().getName();
		msig = (MethodSignature) sig;
		Object target = point.getTarget();
		Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
		// ServletRequestAttributes attributes = (ServletRequestAttributes)
		// RequestContextHolder.getRequestAttributes();

		RedisLock redisLock = currentMethod.getAnnotation(RedisLock.class);
		String constant = redisLock.constant();
		String[] parameters = redisLock.parameters();
		// 获取参数列表
		// 方法名
		// String methodName = currentMethod.getName();
		// String className = point.getTarget().getClass().getName();
		Object[] params = point.getArgs();
		String[] names = ((CodeSignature) point.getSignature()).getParameterNames();
		String key = UtilCacheKey.getKey(constant, names, params, parameters);
		// 执行方法
		Object rs = null;

		if (!redisMapper.lock(key, redisLock.timeout())) {
			throw new BusException(ResultEnum.REDIS_LPCK_ERR);
		}
		try {
			rs = point.proceed();
		} finally {
			redisMapper.unlock(key);
		}

		return rs;
	}
}
