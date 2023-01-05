package com.lzb.rock.mqtt.model;


import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Document(collection = "sequence")
public class Sequence {

	@ApiModelProperty(value = "主键ID")
	@Indexed
	ObjectId _id;

	@ApiModelProperty(value = "设备唯一ID")
	@Indexed
	String clientId;

	@ApiModelProperty(value = "类型")
	String type;

	@ApiModelProperty(value = "下标值")
	Long index;


}
