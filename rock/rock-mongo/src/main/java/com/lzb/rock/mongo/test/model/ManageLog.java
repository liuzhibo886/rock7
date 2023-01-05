package com.lzb.rock.mongo.test.model;


import java.util.Date;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;


@Document(collection="manageLog")  
@Data
public class ManageLog {

	@Id
	ObjectId _id;
	
	String name;
	
	Map<String,Object> map;
	
	Integer taskId;
	
	Integer count;
	
	
	JSONObject jsonResult;
	
	CallBackDto callBackDto;

	Date sendTime;

	Date returnTime;
	
	Date callbackTime;


}
