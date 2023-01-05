package com.lzb.rock.mqtt.client;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Test2 {

	public static void main(String[] args) throws MqttException {

		 MemoryPersistence memoryPersistence = new MemoryPersistence();
		String clientId = "111";
		String endPoint = "127.0.0.1";
		
		String userName = "lzb";
		char[] password = "11111".toCharArray();


		 MqttClient mqttClient = new MqttClient("tcp://" + endPoint + ":1883", clientId, memoryPersistence);
		/**
		 * 客户端设置好发送超时时间，防止无限阻塞
		 */
		mqttClient.setTimeToWait(5000);
		mqttClient.setCallback(new MqttCallbackExtended() {
			@Override
			public void connectComplete(boolean reconnect, String serverURI) {
				/**
				 * 客户端连接成功后就需要尽快订阅需要的 topic
				 */
				System.out.println("connect success");
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
				System.out
						.println("receive msg from topic " + s + " , body is " + new String(mqttMessage.getPayload()));
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
				System.out.println("send msg succeed topic is : " + iMqttDeliveryToken.getTopics()[0]);
			}
		});

		MqttConnectOptions options=new MqttConnectOptions();
		options.setUserName(userName);
		options.setPassword(password);
		
		mqttClient.connect(options);

	}

}
