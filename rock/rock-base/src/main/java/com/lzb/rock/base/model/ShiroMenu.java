/**
 * @author lzb
 *
 * 
 *2019年4月3日 下午11:18:59
 */
package com.lzb.rock.base.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 自定义菜单对象
 * 
 * @author lzb
 * 
 *         2019年4月3日 下午11:18:59
 */
@Data
public class ShiroMenu implements Comparable<ShiroMenu>, Serializable {
	/**
	 * @author lzb
	 *
	 * 
	 *         2019年4月18日 上午12:22:01
	 */
	private static final long serialVersionUID = -1789864979749189903L;
	/**
	 * 菜单ID
	 */
	Long menuId;

	/**
	 * 菜单名称
	 */
	String menuName;
	/**
	 * 菜单编码
	 */
	String menuCode;

	/**
	 * 菜单编码
	 */
	String menuParentCode;
	/**
	 * 菜单图标
	 */
	String menuIcon;

	/**
	 * 菜单访问相对地址
	 */
	String menuUrl;


	/**
	 * 子节点
	 */
	List<ShiroMenu> menuChild;

	@Override
	public int compareTo(ShiroMenu o) {
		if (o == null) {
			return 1;
		} else if (this.menuId.equals(o.menuId)) {
			return 0;
		} else if (this.menuId > o.menuId) {
			return 1;
		} else if (this.menuId < o.menuId) {
			return -1;
		}
		return -1;
	}

}
