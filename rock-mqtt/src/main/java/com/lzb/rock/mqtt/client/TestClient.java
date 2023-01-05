package com.lzb.rock.mqtt.client;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.beanutils.converters.IntegerArrayConverter;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestClient {

//	String serverURI = "tcp://127.0.0.1:1883";
	String serverURI = "tcp://82.157.167.82:1883";

	public MqttClient createMqttClient(String clientId, String topicFilter) throws Exception {

		MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();

		mqttConnectOptions.setUserName("userName");
		mqttConnectOptions.setCleanSession(false);
		
		//设置断线重连
		mqttConnectOptions.setAutomaticReconnect(true);


		MemoryPersistence memoryPersistence = new MemoryPersistence();
		/**
		 * 客户端使用的协议和端口必须匹配，具体参考文档
		 * https://help.aliyun.com/document_detail/44866.html?spm=a2c4g.11186623.6.552.25302386RcuYFB
		 * 如果是 SSL 加密则设置ssl://endpoint:8883
		 */
		MqttClient mqttClient = new MqttClient(serverURI, clientId, memoryPersistence);
		/**
		 * 客户端设置好发送超时时间，防止无限阻塞
		 */
		mqttClient.setTimeToWait(30000);
		mqttClient.setCallback(new MqttCallbackExtended() {
			@Override
			public void connectComplete(boolean reconnect, String serverURI) {
				/**
				 * 客户端连接成功后就需要尽快订阅需要的 topic
				 */
				log.info("连接成功：{}", clientId);
				// 订阅
				try {
					mqttClient.subscribe(new String[] { "test1", "test2" }, new int[] { 1, 2 });
					// log.info("订阅：{} 成功", topicFilter);

				} catch (MqttException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void connectionLost(Throwable throwable) {
				throwable.printStackTrace();
			}

			@Override
			public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
				/**
				 * 消费消息的回调接口，需要确保该接口不抛异常，该接口运行返回即代表消息消费成功。
				 * 消费消息需要保证在规定时间内完成，如果消费耗时超过服务端约定的超时时间，对于可靠传输的模式，服务端可能会重试推送，业务需要做好幂等去重处理。超时时间约定参考限制
				 * https://help.aliyun.com/document_detail/63620.html?spm=a2c4g.11186623.6.546.229f1f6ago55Fj
				 */

				log.info("收到消息topic:{};body：{};id:{}", s, new String(mqttMessage.getPayload()), mqttMessage.getId());
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

				log.info("发送消息成功messageId:{};topic：{}", iMqttDeliveryToken.getMessageId(),
						JSONObject.toJSONString(iMqttDeliveryToken.getTopics()));
			}
		});
		mqttClient.connect(mqttConnectOptions);

		return mqttClient;
	}

	public static void main(String[] args) throws Exception {
		TestClient t = new TestClient();

		Integer maxIndex = 500;
		Integer threadMaxCount = 500;

		AtomicInteger index = new AtomicInteger(0);

		for (int i = 0; i < threadMaxCount; i++) {

			Runnable runnable = new Runnable() {
				public void run() {
					while (true) {
						int nameIndex = index.incrementAndGet();
						if (nameIndex >= maxIndex) {
							break;
						}
						try {
							MqttClient mqttClient = t.createMqttClient("lzb_test_" + nameIndex, "test");
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			};
			new Thread(runnable).start();
		}

//		MqttClient mqttClient = t.createMqttClient("deviceId_lzb01", "test");
//		Scanner input = new Scanner(System.in);
//
//		while (true) {
//			System.out.println("输入发送消息内容==>>");
//			String text = input.next();// 读取输入的字符串
//
//			MqttMessage message = new MqttMessage(text.getBytes());
//			message.setQos(2);
//			mqttClient.publish("test2", message);
//			System.out.println("发送消息==>" + text);
//		}

	}

}
