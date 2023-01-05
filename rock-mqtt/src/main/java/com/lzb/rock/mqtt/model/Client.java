package com.lzb.rock.mqtt.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Document(collection = "client")
public class Client {

	@ApiModelProperty(value = "主键ID")
	@Indexed
	ObjectId _id;

	@ApiModelProperty(value = "设备唯一ID")
	@Indexed
	String clientId;

	@ApiModelProperty(value = "账号")
	String userName;

	@ApiModelProperty(value = "密码")
	String password;

	// 0 在线 1 离线
	@ApiModelProperty(value = "在线状态，0 在线，1 离线")
	Integer status;

	// 离线清除
//	@ApiModelProperty(value = "")
	boolean cleanSession;

	@ApiModelProperty(value = "订阅的topic")
	Map<String, Integer> topicMap;

	@ApiModelProperty(value = "最后修改时间")
	Date lastTime;

}
