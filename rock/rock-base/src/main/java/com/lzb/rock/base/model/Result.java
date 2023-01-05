package com.lzb.rock.base.model;

import java.io.Serializable;

import com.lzb.rock.base.enums.ResultEnum;
import com.lzb.rock.base.facade.IBaseResultEnum;
import com.lzb.rock.base.util.UtilDate;
import com.lzb.rock.base.util.UtilJson;

import io.swagger.annotations.ApiModelProperty;

/**
 * 返回基类
 * 
 * @author lzb 2018年2月1日 下午3:44:20
 * @param <T>
 */
public class Result<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 返回码
	 */
	@ApiModelProperty(value = "返回码，成功为success")
	private String code;

	@ApiModelProperty(value = "请求ID")
	private String requestId;
	/**
	 * 错误消息
	 */
	@ApiModelProperty(value = "返回码说明")
	private String msg;
	/**
	 * 返回结果内容
	 */
	@ApiModelProperty(value = "返回内容对象")
	private T data;

	@ApiModelProperty(value = "时间戳")
	private String time = UtilDate.getFomtTimeByDateString();
	/**
	 * 自定义错误提醒
	 */
	@ApiModelProperty(value = "自定义错误提醒，逗号分隔")
	private StringBuffer errMsgs = new StringBuffer();

	/**
	 * 操作成功
	 * 
	 * @param data 内容
	 */
	public static <T> Result<T> success(T data) {
		Result<T> rs = new Result<T>(ResultEnum.SUCCESS);
		rs.data = data;
		return rs;
	}

	/**
	 * 操作成功
	 * 
	 */
	public static <T> Result<T> success() {
		Result<T> rs = new Result<T>(ResultEnum.SUCCESS);
		return rs;
	}

	/**
	 * 操作失败
	 * 
	 * @param resultEnum 错误 enum
	 */
	public static <T> Result<T> newInstance(IBaseResultEnum resultEnum, Class<T> clazz) {
		Result<T> rs = new Result<T>();
		rs.setEnum(resultEnum);
		return rs;
	}

	public static <T> Result<T> newInstance(IBaseResultEnum resultEnum) {
		Result<T> rs = new Result<T>();
		rs.setEnum(resultEnum);
		return rs;
	}

	public static <T> Result<T> newInstance(IBaseResultEnum resultEnum, String msg) {
		Result<T> rs = new Result<T>();
		rs.setEnum(resultEnum);
		rs.setMsg(msg);
		return rs;
	}

	/**
	 * 操作失败
	 * 
	 * @param msgEnum 错误 enum
	 * @param errMsg  自定义错误提醒
	 */
	public static <T> Result<T> newInstance(IBaseResultEnum msgEnum, String msg, Class<T> clazz) {
		Result<T> rs = new Result<T>();
		rs.setEnum(msgEnum);
		rs.setMsg(msg);
		return rs;
	}

	/**
	 * 
	 * @param msgEnum 错误 enum
	 * @param errMsg  自定义错误提醒
	 * @param data    内容
	 */
	public static <T> Result<T> newInstance(IBaseResultEnum msgEnum, String errMsg, T data) {
		Result<T> rs = new Result<T>();
		rs.setEnum(msgEnum);
		rs.setErrMsgs(errMsg);
		rs.setData(data);
		return rs;
	}
	
	public static <T> Result<T> newInstance(IBaseResultEnum msgEnum, T data) {
		Result<T> rs = new Result<T>();
		rs.setEnum(msgEnum);
		rs.setData(data);
		return rs;
	}

	/**
	 * 自定义错误消息
	 * 
	 * @param errMsg
	 */
	public void setErrMsgs(String errMsg) {
		if (errMsgs.length() > 0) {
			errMsgs.append(",").append(errMsg);
		} else {
			errMsgs.append(errMsg);
		}
	}

	/**
	 * 校验前一步是否错误 true 为正确
	 * 
	 * @return
	 */
	public Boolean check() {
		return ResultEnum.SUCCESS.getCode().equals(this.code);
	};

	/**
	 * 请求成功且返回值不为null
	 * 
	 * @return
	 */
	public Boolean checkAndNotNull() {
		if (ResultEnum.SUCCESS.getCode().equals(this.code) && this.data != null) {
			return true;
		}
		return false;
	};

	/**
	 * 操作成功
	 */
	public Result() {
		setEnum(ResultEnum.SUCCESS);
	};

	public Result(IBaseResultEnum msgEnum, T data) {
		setEnum(msgEnum);

		this.setData(data);
	}

	public Result(IBaseResultEnum msgEnum) {
		this.setCode(msgEnum.getCode());
		this.setMsg(msgEnum.getMsg());
	}

	/**
	 * 通过反射获取枚举
	 * 
	 * @param msgEnum
	 */
	public void setEnum(IBaseResultEnum msgEnum) {
		this.setCode(msgEnum.getCode());
		this.setMsg(msgEnum.getMsg());
	}

	public void setEnum(IBaseResultEnum msgEnum, String msg) {
		this.setCode(msgEnum.getCode());
		this.setMsg(msg);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public StringBuffer getErrMsgs() {
		return errMsgs;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setErrMsgs(StringBuffer errMsgs) {
		this.errMsgs = errMsgs;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@Override
	public String toString() {
		return UtilJson.getStr(this);
	}

}
