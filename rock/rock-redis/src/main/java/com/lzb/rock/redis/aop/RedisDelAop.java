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
import com.lzb.rock.redis.aop.annotation.RedisDel;
import com.lzb.rock.redis.mapper.RedisMapper;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Order(2)
@Slf4j
public class RedisDelAop {

	@Autowired
	RedisMapper redisMapper;

	@Pointcut(value = "@annotation(com.lzb.rock.redis.aop.annotation.RedisDel)")
	public void redisDel() {
	}

	@Around("redisDel()")
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

		RedisDel redisDel = currentMethod.getAnnotation(RedisDel.class);
		String constant = redisDel.constant();
		String[] parameters = redisDel.parameters();
		// 获取参数列表
		// 方法名
		// String methodName = currentMethod.getName();
		// String className = point.getTarget().getClass().getName();
		Object[] params = point.getArgs();
		String[] names = ((CodeSignature) point.getSignature()).getParameterNames();
		String key = UtilCacheKey.getKey(constant, names, params, parameters);
		// 执行方法
		Object rs = point.proceed();
		// 生成缓存key值
		redisMapper.del(key);
		log.debug("删除缓存key值：{}", key);
		return rs;
	}
}
