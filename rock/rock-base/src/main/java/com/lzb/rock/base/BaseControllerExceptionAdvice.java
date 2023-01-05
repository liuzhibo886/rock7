package com.lzb.rock.base;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.lzb.rock.base.enums.ResultEnum;
import com.lzb.rock.base.exception.BusClientException;
import com.lzb.rock.base.exception.BusException;
import com.lzb.rock.base.model.Result;
import com.lzb.rock.base.util.UtilExceptionStackTrace;
import com.lzb.rock.base.util.UtilHttpKit;
import com.lzb.rock.base.util.UtilString;

/**
 * 全局异常处理器
 * 
 * @author lzb
 * @Date 2019年7月31日 下午4:45:41
 */
@ControllerAdvice
public abstract class BaseControllerExceptionAdvice {
	protected Logger log = LoggerFactory.getLogger(BaseControllerExceptionAdvice.class);

	/**
	 * 处理自定义业务异常
	 * 
	 * @param ex
	 * @param request
	 */
	@ExceptionHandler(BusException.class)
	@ResponseBody
//	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Result<String> BusException(BusException ex) {
		Result<String> result = new Result<String>();
		result.setData(ex.getData());
		result.setCode(ex.getCode());
		result.setMsg(ex.getMessage());
		log.warn("BusException;uri:{},{},Parameters,ex:{}", UtilHttpKit.getRequestUri(),
				UtilHttpKit.getRequestParameters(), result, UtilExceptionStackTrace.getStackTrace(ex));
		return result;
	}

	@ExceptionHandler(UndeclaredThrowableException.class)
	@ResponseBody
//	 @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Result<Void> undeclaredThrowableException(UndeclaredThrowableException ex) {
		Result<Void> result = Result.newInstance(ResultEnum.SYSTTEM_ERR, "服务器繁忙");
		log.warn("uri:{},{},Parameters:{},ex:{}", UtilHttpKit.getRequestUri(), UtilHttpKit.getRequestParameters(),
				result, UtilExceptionStackTrace.getStackTrace(ex));
		return result;
	}

	@ExceptionHandler(BusClientException.class)
	@ResponseBody
//	 @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Result<String> rockClientException(BusClientException ex) {
		Result<String> result = new Result<String>();
		result.setCode(ex.getCode());
		result.setMsg("服务器繁忙,请稍后再试");
		log.warn("rockClientException;uri:{},{}", UtilHttpKit.getRequestUri(), result);
		log.warn(UtilExceptionStackTrace.getStackTrace(ex));
		return result;
	}

	/**
	 * 处理参数校验异常
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public Result<String> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		String uri = UtilHttpKit.getRequestUri();
		BindingResult bindingResult = ex.getBindingResult();
		List<ObjectError> errors = bindingResult.getAllErrors();
		StringBuffer sb = new StringBuffer();
		boolean flag1 = false;
		String errMsg = "";
		for (ObjectError objectError : errors) {

			if (flag1) {
				sb.append(";");
			} else {
				flag1 = true;
			}
			sb.append(objectError.getDefaultMessage()).append(",");
			if (UtilString.isNotBlank(errMsg)) {
				errMsg = errMsg + ";";
			}

			errMsg = errMsg + objectError.getDefaultMessage();
			// 获取报错的值
			if (objectError instanceof FieldError) {
				FieldError fieldError = (FieldError) objectError;
				sb.append(fieldError.getRejectedValue()).append(",");
			}
			// 获取错误信息
			String[] codes = objectError.getCodes();
			if (codes.length > 0) {
				sb.append(codes[0]);
			}
			Object[] arguments = objectError.getArguments();
			if (arguments != null && arguments.length > 2) {
				sb.append(",").append(arguments[1]).append(",").append(arguments[2]);
			}
		}
		Result<String> result = new Result<String>();
		if (UtilString.isBlank(errMsg)) {
			result.setEnum(ResultEnum.PAEAM_ERR);
		} else {
			result.setEnum(ResultEnum.PAEAM_ERR, errMsg);
		}
		result.setErrMsgs(sb.toString());

		log.debug("{}", sb.toString());
		log.debug("{}", result);
		log.debug("uri={}", uri);
		log.debug(UtilExceptionStackTrace.getStackTrace(ex));
		return result;
	}

	/**
	 * 参数类型不匹配
	 * 
	 * @param ex
	 * @param request
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseBody
	public Result<String> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		Result<String> result = new Result<String>();
		result.setEnum(ResultEnum.PAEAM_ERR, "参数类型异常");
		result.setErrMsgs(UtilExceptionStackTrace.getStackTrace(ex));
		log.info("methodArgumentTypeMismatchException;uri:{},{}", UtilHttpKit.getRequestUri(), result);
		log.info(UtilExceptionStackTrace.getStackTrace(ex));
		return result;
	}

	/**
	 * 处理运行时异常
	 * 
	 * @param ex
	 * @param request
	 */
	@ExceptionHandler(RuntimeException.class)
	@ResponseBody
	public Result<String> runtimeException(RuntimeException ex) {
		String message = UtilExceptionStackTrace.getStackTrace(ex);
		Result<String> result = new Result<String>();
		log.error("runtimeException;uri:{},{}", UtilHttpKit.getRequestUri(), result);
		log.error(message);

		if (StringUtils.isNotBlank(message) && message.indexOf("com.netflix.client.ClientException") > -1) {
			log.info(UtilExceptionStackTrace.getStackTrace(ex));
			result.setEnum(ResultEnum.REST_ERR, "服务器繁忙,请稍后再试");
		} else {
			log.info(UtilExceptionStackTrace.getStackTrace(ex));
			result.setEnum(ResultEnum.RUNTIME_ERR, "服务器繁忙,请稍后再试");
		}
		result.setErrMsgs(UtilExceptionStackTrace.getStackTrace(ex));

		return result;
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseBody
	public Result<String> MissingServletRequestParameterException(MissingServletRequestParameterException ex) {
		String message = UtilExceptionStackTrace.getStackTrace(ex);

		String uri = UtilHttpKit.getRequestUri();
		Result<String> result = new Result<String>();
		result.setEnum(ResultEnum.PAEAM_ERR, "参数异常");
		result.setErrMsgs(ex.getParameterName() + "不能为空");
		log.error("MissingServletRequestParameterException;uri:{},{}", UtilHttpKit.getRequestUri(), result);
		log.error(message);
		return result;
	}

	/**
	 * 参数异常
	 * 
	 * @param ex
	 * @param request
	 */
	@ExceptionHandler(NoSuchMethodException.class)
	@ResponseBody
	public Result<String> NoSuchMethodException(NoSuchMethodException ex) {
		String message = UtilExceptionStackTrace.getStackTrace(ex);

		Result<String> result = new Result<String>();
		result.setEnum(ResultEnum.SYSTTEM_ERR, "服务器繁忙,请稍后再试");
		result.setErrMsgs(UtilExceptionStackTrace.getStackTrace(ex));
		log.warn("NoSuchMethodException;uri:{},{},Parameters,ex:{}", UtilHttpKit.getRequestUri(),
				UtilHttpKit.getRequestParameters(), result, UtilExceptionStackTrace.getStackTrace(ex));
		log.error(message);
		return result;

	}

	/**
	 * 处理系统异常
	 * 
	 * @param ex
	 * @param request
	 */
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public Result<String> exception(Exception ex) {
		String message = UtilExceptionStackTrace.getStackTrace(ex);
		Result<String> result = new Result<String>();
		result.setEnum(ResultEnum.SYSTTEM_ERR, "服务器繁忙,请稍后再试");
		result.setErrMsgs(UtilExceptionStackTrace.getStackTrace(ex));
		log.error("exception;uri:{},{}", UtilHttpKit.getRequestUri(), result);
		log.error(message);

		return result;
	}

}
