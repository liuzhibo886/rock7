package com.lzb.rock.mongo.test.model;

import lombok.Data;

@Data
public class CallBackDto {

	
	String tags; 
	String keys; 
	String data; 
	String loginData; 
	Integer ret;
	String minTime;
	String msg;
	
	String uid;

	
	String proxy_host;
}
