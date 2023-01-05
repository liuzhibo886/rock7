package com.lzb.rock.base.model;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页请求参数
 * 
 * @author lzb
 * @Date 2019年8月8日 下午4:45:09
 */
@Data
@ApiModel(value = "分页返回基类")
public class PageResp<T> {
	
	public PageResp() {
		
	}
	
	public PageResp(IPage<T> page) {
		this.total=page.getTotal();
		this.size=page.getSize();
		this.current=page.getCurrent();
		this.records=page.getRecords();
	}
	
	/**
	 * 
	 * @param total 总条数
	 * @param size 每页显示条数
	 * @param current 当前页
	 * @param records 当前数据
	 */

	public PageResp(long total, long size, long current, List<T> records) {
		super();
		this.total = total;
		this.size = size;
		this.current = current;
		this.records = records;
	}



	@ApiModelProperty(value = "总条数")
	long total;

	@ApiModelProperty(value = "每页显示条数")
	long size;

	@ApiModelProperty(value = "当前页")
	long current;

	@ApiModelProperty(value = "当前数据")
	List<T> records;
	
	
	
}
