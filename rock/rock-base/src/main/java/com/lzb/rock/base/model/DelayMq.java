package com.lzb.rock.base.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 延迟消息
 * 
 * @author lzb
 * @date 2020年9月17日下午5:57:21
 */
@Data
public class DelayMq {
	
	@NotBlank(message = "topic 不能为空")
	String topic;

	@NotBlank(message = "tag 不能为空")
	String tag;

	@ApiModelProperty(value = "消息内容")
	@NotBlank(message = "消息内容 不能为空")
	String message;

	@ApiModelProperty(value = "延迟时间,毫秒,延迟时间和到期时间必须二选一")
	@NotNull(message = "延迟时间和到期时间必须有一个不为空",groups = {DelayMq.class})
	Long delayTime;
	
	@ApiModelProperty(value = "到期时间,时间戳,延迟时间和到期时间必须二选一")
	@NotNull(message = "延迟时间和到期时间必须有一个不为空",groups = {DelayMq.class})
	Long dueTime;

	@ApiModelProperty(value = "超时时间,毫秒")
	Long overtime = 30000L;



}
