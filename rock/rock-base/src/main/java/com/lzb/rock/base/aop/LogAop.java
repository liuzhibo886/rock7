package com.lzb.rock.base.aop;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lzb.rock.base.aop.annotation.Log;
import com.lzb.rock.base.model.Result;
import com.lzb.rock.base.util.UtilClass;
import com.lzb.rock.base.util.UtilHttpKit;

import lombok.extern.slf4j.Slf4j;

/**
 * 日志aop
 * 
 * @author lzb
 * @Date 2019年7月31日 下午4:46:08
 */
@Aspect
@Component
@Order(1)
@Slf4j
@ConditionalOnProperty(prefix = "rock.log", value = "enabled", havingValue = "true", matchIfMissing = true)
public class LogAop {

	@Pointcut(value = "execution(* com.lzb.rock..controller..*.*(..))")
	public void controller() {  
	}

	@Pointcut(value = "execution(* com.lzb.rock.service..*.*(..))")
	public void service() {
	}

	@Around("controller()")
	private Object controllerHandle(ProceedingJoinPoint point) throws Throwable {
		return handle(point);
	}

	@Around("service()")
	private Object serviceHandle(ProceedingJoinPoint point) throws Throwable {

		return handle(point);
	}

	private Object handle(ProceedingJoinPoint point) throws Throwable {

		JSONObject logStartJson = new JSONObject();
		// 获取拦截的方法名
		Signature sig = point.getSignature();
		MethodSignature msig = null;
//		if (!(sig instanceof MethodSignature)) {
//			throw new BusException(ResultEnum.AOP_ERR, "该注解只能用于方法");
//		}
		if (sig instanceof MethodSignature) {
			msig = (MethodSignature) sig;
		}
		if (msig == null) {
			return point.proceed();
		}

		// 类完整路径
		String classType = point.getTarget().getClass().getName();
		// 判断

		Method currentMethod = msig.getMethod();

		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		// 方法名
		String methodName = currentMethod.getName();
		// 完整方法路径
		String path = classType + "." + methodName;
		// 获取拦截方法的参数
		Object[] params = point.getArgs();
		String[] names = ((CodeSignature) point.getSignature()).getParameterNames();
		logStartJson.put("path", path);
		// 获取注解参数
		Log logAnnotation = currentMethod.getAnnotation(Log.class);
		boolean before = true;
		boolean end = true;
		boolean endData = true;
		String methodNameaAias = "";
		if (logAnnotation != null) {
			before = logAnnotation.before();
			end = logAnnotation.end();
			endData = logAnnotation.endData();
			methodNameaAias = logAnnotation.name();
			if (StringUtils.isNotBlank(methodNameaAias)) {
				logStartJson.put("methodNameaAias", methodNameaAias);
			}
		}
		// 判断是否打印入参
		if (before) {
			// 判断是否为action 或者controller,若是打印访问url
			if (classType != null) {
				String classType1 = classType.toLowerCase();
				if (attributes != null
						&& (classType1.indexOf("action") > -1 || classType1.indexOf("controller") > -1)) {

					HttpServletRequest request = attributes.getRequest();
					String uri = request.getRequestURI();
					String method = request.getMethod();
					String ip = UtilHttpKit.getIpAddr();
					String userId = request.getHeader("userId");
					String appVersion = request.getHeader("appVersion");
					String platform = request.getHeader("platform");

					JSONObject head = new JSONObject();
					// 记录下请求内容
					head.put("ip", ip);
					head.put("method", method);
					head.put("uri", uri);
					head.put("userId", userId);
					head.put("appVersion", appVersion);
					head.put("platform", platform);
					logStartJson.put("head", head);
					// 获取request的参数
				}
				// 获取方法参数
				if (params != null) {
					int size = params.length;
					for (int i = 0; i < size; i++) {
						Object param = params[i];
						String name = names[i];
						if (param instanceof HttpServletRequest || param instanceof HttpServletResponse || param instanceof MultipartFile) {
							continue;
						}
						logStartJson.put(name, param);
					}
				}
			}
			// 打印参数
			log.info(logStartJson.toJSONString());
		}
		// 执行方法
		Object rs = point.proceed();
		// 完全不打印返回值
		if (end) {
			JSONObject logEndJson = new JSONObject();
			logEndJson.put("path", path);
			if (endData) {
				logEndJson.put("result", rs);
			} else if (rs != null && rs instanceof Result) {
				JSONObject obj = new JSONObject();
				obj.put("code", UtilClass.getFieldValue("code", rs));
				obj.put("msg", UtilClass.getFieldValue("msg", rs));
				obj.put("errMsgs", UtilClass.getFieldValue("errMsgs", rs));
				logEndJson.put("result", obj);
			}

			// 是否打印返回值
			logEndJson.put("path", path);
			if (attributes != null) {
				HttpServletResponse resp = attributes.getResponse();
				String head = resp.getHeader("head");
				logEndJson.put("head", head);

				// JSONObject.DEFFAULT_DATE_FORMAT="yyyy-MM-dd";//设置日期格式
				String jsonStr = JSONObject.toJSONString(logEndJson, SerializerFeature.WriteMapNullValue,
						SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat);
				log.info(jsonStr);
			}
		}

		return rs;
	}

}
