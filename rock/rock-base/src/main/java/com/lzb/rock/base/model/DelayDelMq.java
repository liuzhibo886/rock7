package com.lzb.rock.base.model;

import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 延迟撤回,delayId msgId 二选一
 * 
 * @author lzb
 * @date 2020年9月17日下午5:57:21
 */
@Data
public class DelayDelMq {

	@ApiModelProperty(value = "延迟消息ID")
	@NotNull(message = "延迟消息ID不能为空", groups = DelayDelMq.class)
	ObjectId delayId;

	@ApiModelProperty(value = "消息队列ID")
	@NotNull(message = "消息队列ID不能为空", groups = DelayDelMq.class)
	
	String msgId;
}
