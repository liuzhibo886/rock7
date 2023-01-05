package com.lzb.rock.base.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页请求参数
 * 
 * @author lzb
 * @Date 2019年8月8日 下午4:45:09
 */
@Data
public class PageReq implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "每页多少条", required = true)
	@TableField(exist = false)
	Integer limit = 10;

	@ApiModelProperty(value = "每页多少条", required = true)
	@TableField(exist = false)
	Integer page = 1;

	@ApiModelProperty(value = "偏移量", required = true)
	@TableField(exist = false)
	Integer offset = 0;
}
