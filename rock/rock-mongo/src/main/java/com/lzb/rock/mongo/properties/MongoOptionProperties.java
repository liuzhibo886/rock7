package com.lzb.rock.mongo.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "spring.data.mongodb.option")
@EnableConfigurationProperties
@Configuration
public class MongoOptionProperties {

	/**
	 * 最小连接数
	 */
	private Integer minConnectionSize = 2;
	/**
	 * 最大连接数
	 */
	private Integer maxConnectionSize = 100;
	/**
	 * 设置线程阻塞等待连接的最长时间，单位秒
	 */
	private Integer maxWaitTime = 60 * 10;
	
	/**
	 * 设置池连接的最大空闲时间。最大空闲时间，以秒为单位，必须大于等于0，0表示寿命没有限制
	 */
	private Integer maxConnectionIdleTime = 60 * 10;
	
	/**
	 * 设置池连接的最长生存时间，单位秒，小于等于0 为无限制
	 */
	private Integer maxConnectionLifeTime = 60 * 60 * 8;
	
	
	
	
	
//	
//	/**
//	 * 每个host允许链接的最大链接数,这些链接空闲时会放入池中,如果链接被耗尽，任何请求链接的操作会被阻塞等待链接可用,
//	 */
//	private Integer connectionsPerHost = 100;
//	/**
//	 * 此参数跟connectionsPerHost的乘机为一个线程变为可用的最大阻塞数，超过此乘机数之后的所有线程将及时获取一个异常.eg.connectionsPerHost=10
//	 * and threadsAllowedToBlockForConnectionMultiplier=5,最多50个线程等级一个链接，推荐配置为5
//	 */
//	private Integer threadsAllowedToBlockForConnectionMultiplier = 5;
//
//	/**
//	 * 服务器查找超时时间
//	 */
//	private Integer serverSelectionTimeout = 10;
//
//	/**
//	 * 连接超时时间，秒
//	 */
//	private Integer connectTimeout = 10;
//	/**
//	 * 连接超时时间，秒
//	 */
//	private Integer socketTimeout = 10;
//	/**
//	 * *设置是否使用SSL。
//	 */
//	private Boolean sslEnabled = false;
//	/**
//	 * 定义是否应允许无效主机名
//	 */
//	private Boolean sslInvalidHostNameAllowed = false;
//	/**
//	 * 设置心跳频率。这是驱动程序尝试确定中每个服务器的当前状态的频率，秒
//	 */
//	private Integer heartbeatFrequency =  10;
//	/**
//	 * 心跳连接超时时间
//	 */
//	private Integer heartbeatConnectTimeout =  20;
//	/**
//	 * 设置用于群集检测信号的连接的套接字超时
//	 */
//	private Integer heartbeatSocketTimeout =  20;
//	/**
//	 * 可接受的延迟差（以秒为单位），必须大于等于0
//	 * 
//	 * @return{@code this}
//	 */
//	private Integer localThreshold = 15;

}