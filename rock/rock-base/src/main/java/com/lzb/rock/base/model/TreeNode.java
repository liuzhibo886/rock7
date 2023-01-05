/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzb.rock.base.model;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * layui 树对象
 * 
 * @author lzb
 * @Date 2019年9月16日 上午10:37:00
 */
@Data
public class TreeNode implements Comparable<TreeNode> {

	@ApiModelProperty(value = "ID 唯一")
	String id;
	
	@ApiModelProperty(value = "标题")
	String title;
	
	@ApiModelProperty(value = "父级ID，组装数据时使用", hidden = true)
	String pId;
	
	@ApiModelProperty(value = "字典编码，组装数据时使用", hidden = true)
	private String code;
	
	@ApiModelProperty(value = "节点是否初始展开，默认 false")
	Boolean spread = false;
	
	@ApiModelProperty(value = "节点是否初始为选中状态（如果开启复选框的话），默认 false")
	Boolean checked = false;
	
	@ApiModelProperty(value = "节点是否为禁用状态。默认 false")
	Boolean disabled = false;
	
	@ApiModelProperty(value = "节点字段名;一般对应表字段名")
	String field;
	
	@ApiModelProperty(value = "点击节点弹出新窗口对应的 url。需开启 isJump 参数")
	String href;
	
	@ApiModelProperty(value = "排序字段，按照升序排列")
	Integer sort = 0;
	
	@ApiModelProperty(value = "子节点数据")
	List<TreeNode> children;

	@Override
	public int compareTo(TreeNode arg0) {
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
