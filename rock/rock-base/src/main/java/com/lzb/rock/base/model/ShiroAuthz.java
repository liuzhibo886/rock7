package com.lzb.rock.base.model;

import java.util.Objects;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.AllPermission;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 菜单表
 * </p>
 * 
 * @author lzb123
 * @since 2019-05-16
 */
@Data
public class ShiroAuthz extends AllPermission implements Permission {

	/**
	 * @author lzb
	 *
	 * 
	 *         2019年5月27日 下午10:33:04
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 权限ID
	 */
	@ApiModelProperty(value = "权限ID")
	private Long authzId;
	/**
	 * 权限编码
	 */
	@ApiModelProperty(value = "权限编码")
	private String authzCode;
	/**
	 * 权限父编码
	 */
	@ApiModelProperty(value = "权限父编码")
	private String authzParentcode;
	/**
	 * 权限名称
	 */
	@ApiModelProperty(value = "权限名称")
	private String authzName;
	/**
	 * 权限图片，菜单权限专用
	 */
	@ApiModelProperty(value = "权限图片，菜单权限专用")
	private String authzIcon;

	/**
	 * 权限url地址，菜单权限专用
	 */
	@ApiModelProperty(value = "权限url地址，菜单权限专用")
	private String authzUrl;

	/**
	 * 权限排序，升序
	 */
	@ApiModelProperty(value = "权限排序，升序")
	private Integer authzSort;

	/**
	 * 权限类型，0菜单，1按钮2字段
	 */
	@ApiModelProperty(value = "权限类型，0菜单，1按钮，2字段")
	private Integer authzType;

	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String authzTips;

	/**
	 * 是否打开: 1:打开 0:不打开；菜单权限专用
	 */
	@ApiModelProperty(value = "是否打开:    1:打开   0:不打开；菜单权限专用")
	private Integer authzIsOpen;
	@Override
	public int hashCode() {

		return Objects.hash(this.authzId);
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ShiroAuthz shiroAuthz = (ShiroAuthz) o;

		return shiroAuthz.getAuthzCode().equals(this.authzCode);
	}

	@Override
	public boolean implies(Permission p) {
		// TODO Auto-generated method stub
		return false;
	}
}
