package com.lzb.rock.base.exception;

import com.lzb.rock.base.facade.IBaseResultEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 封装rock的异常
 *
 * @author lzb
 * @Date 2017/12/28 下午10:32
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String code;

	private String message;

	String data;

	public BusException(String code, String message) {
		this.code = code;
		this.message =message;
	}
	

	public BusException(IBaseResultEnum busEnum) {
		this.code = busEnum.getCode();
		this.message = busEnum.getMsg();
	}

	public BusException(IBaseResultEnum busEnum, String message) {
		this.code = busEnum.getCode();
		this.message = message;
	}

	public BusException(IBaseResultEnum busEnum, String message, String data) {
		this.code = busEnum.getCode();
		this.message = message;
		this.data = data;
	}

}
