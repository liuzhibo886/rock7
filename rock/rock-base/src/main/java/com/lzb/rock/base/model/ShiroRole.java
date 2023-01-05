/**
 * @author lzb
 *
 * 
 *2019年4月3日 下午11:18:59
 */
package com.lzb.rock.base.model;

import java.io.Serializable;

import lombok.Data;

/**
 * 自定义角色对象
 * 
 * @author lzb
 * 
 *         2019年4月3日 下午11:18:59
 */
@Data
public class ShiroRole implements Serializable {
	/**
	 * @author lzb
	 *
	 * 
	 *         2019年4月18日 上午12:22:52
	 */
	private static final long serialVersionUID = 8234821994202318975L;
	/**
	 * 角色ID
	 */
	Long roleId;
	/**
	 * 角色名称
	 */
	String roleName;
	/**
	 * 角色编码
	 */
	String roleCode;

}
