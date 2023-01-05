package com.lzb.rock.mqtt.model;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 服务端收到客户端消息
 * 
 * @author lzb
 *
 */
@Data
@Document(collection = "pubMsg")
public class PubMsg {

	@ApiModelProperty(value = "主键ID")
	@Indexed
	ObjectId _id;

	@ApiModelProperty(value = "设备唯一ID")
	@Indexed
	String clientId;

	@ApiModelProperty(value = "packetId")
	Integer packetId;

	@ApiModelProperty(value = "qos")
	Integer qos;

	@ApiModelProperty(value = "ack")
	Integer ack;
	
	Integer ackCount;

	@ApiModelProperty(value = "topic")
	String topic;
	
	@ApiModelProperty(value = "payload")
	String payload;

	@ApiModelProperty(value = "最后修改时间")
	Date lastTime;

}
