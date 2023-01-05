package com.lzb.rock.base.exception;

/**
 * 服务端抛出BusinessException时，用以传递到API层
 * 
 * @author lzb 2018年6月5日 下午4:03:40
 */
public class BusExceptionResponse {
	/**
	 * 异常代码
	 */
	private String code;

	/**
	 * 异常消息
	 */
	private String message;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
