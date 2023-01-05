package com.lzb.rock.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 请求头内容
 * 
 * @author lzb
 * @date 2020年7月16日上午11:07:00
 */
@Data
public class Header {

	@ApiModelProperty(value = "登录token")
	String token;

	@ApiModelProperty(value = "登录用户ID")
	String userId;

	@ApiModelProperty(value = "来源平台")
	String platform;

	@ApiModelProperty(value = "租户ID")
	String tenantId;
	
	String version;
	
	String deviceId;

	@ApiModelProperty(value = "创建时间")
	String createTime;

	@ApiModelProperty(value = "最后刷新时间")
	Long lastRefTime;

}
