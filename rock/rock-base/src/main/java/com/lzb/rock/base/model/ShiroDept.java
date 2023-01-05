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
 * 自定义部门对象
 * 
 * @author lzb
 * 
 *         2019年4月3日 下午11:18:59
 */
@Data
public class ShiroDept implements Serializable {

	/**
	 * @author lzb
	 *
	 * 
	 *         2019年4月18日 上午12:22:44
	 */
	private static final long serialVersionUID = -2840940814445130011L;

	Long deptId;
	/**
	 * 部门父级编码
	 */
	String deptParentCode;
	
	/**
	 * 部门编码
	 */
	String deptCode;

	String peptName;

}
