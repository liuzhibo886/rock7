package com.lzb.rock.base.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import lombok.Data;

/**
 * 自定义Authentication对象，使得Subject除了携带用户的登录名外还可以携带更多信息
 * 
 * @author lzb
 * 
 *         2019年4月3日 下午11:25:50
 */
@Data
public class ShiroUser implements Serializable {

	private static final long serialVersionUID = 1L;
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
	/**
	 * 密码，在初始化SimpleAuthenticationInfo时需要,shiro 中不会存储，必填
	 */
	public String userPassword;
	/**
	 * 加密随机字符串，在初始化SimpleAuthenticationInfo时需要,shiro 中不会存储，必填
	 */
	public String userSalt;
	/**
	 * 头像
	 */
	public String avatarUrl;
	/**
	 * 部门
	 */
	ShiroDept dept;
	/**
	 * 角色集合
	 */
	List<ShiroRole> roles;
	/**
	 * 菜单集
	 */
	List<ShiroMenu> menus;
	/**
	 * 权限集合
	 */
	Set<ShiroAuthz> authzs;
}
