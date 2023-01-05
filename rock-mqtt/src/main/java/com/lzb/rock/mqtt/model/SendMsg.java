package com.lzb.rock.mqtt.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 服务端给客户端发送消息
 * 
 * @author lzb
 *
 */
@Data
@Document(collection = "sendMsg")
public class SendMsg {

	@ApiModelProperty(value = "主键ID")
	@Indexed
	ObjectId _id;

	
	ObjectId oldId;
	
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
	
	@ApiModelProperty(value = "重试次数")
	Integer count;

	@ApiModelProperty(value = "最后修改时间")
	Date lastTime;

}
