package com.lzb.rock.base.model;

import lombok.Data;

/**
 * 日志保存用户对象
 * 
 * @author lzb
 *
 */
@Data
public class LogUser {
	/**
	 * 主键ID，必填
	 */
	public Long userId;
	/**
	 * 账号，必填
	 */
	public String userAccount;
	/**
	 * 姓名，必填
	 */
	public String userName;
}
