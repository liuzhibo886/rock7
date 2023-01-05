package com.lzb.rock.base.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * select 下拉框对象
 * 
 * @author lzb
 * @Date 2019年9月16日 上午10:37:00
 */
@Data
public class SelectNode implements Comparable<SelectNode> {

	@ApiModelProperty(value = "唯一key")
	String key;
	
	@ApiModelProperty(value = "ID 唯一")
	String value;
	
	@ApiModelProperty(value = "多余单数，对象,MAP均可")
	Object params;
	
	@ApiModelProperty(value = "排序字段")
	Integer sort = 0;
	
	@ApiModelProperty(value = "节点是否为禁用状态，false禁用")
	Boolean disabled = false;

	@Override
	public int compareTo(SelectNode arg0) {
		if (this.sort == null) {
			this.sort = 0;
		}
		if (arg0.sort == null) {
			arg0.sort = 0;
		}
		return this.getSort().compareTo(arg0.getSort());
	}

//	@Override
//	public boolean equals(Object obj) {
//		if (this.id == null) {
//			return false;
//		}
//		if (obj != null && obj instanceof TreeNode) {
//			TreeNode node = (TreeNode) obj;
//			if (node.getId() != null) {
//				return node.getId().equals(this.id);
//			}
//		}
//		return false;
//	}
//
//	@Override
//	public int hashCode() {
//
//		return super.hashCode();
//	}
}
