
package com.lzb.rock.mqtt.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectPayload;
import io.netty.handler.codec.mqtt.MqttConnectVariableHeader;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttHeartBeatClientHandler extends ChannelInboundHandlerAdapter {

	private static final String PROTOCOL_NAME_MQTT_3_1_1 = "MQTT";
	private static final int PROTOCOL_VERSION_MQTT_3_1_1 = 4;

	private final String clientId;
	private final String userName;
	private final byte[] password;

	public MqttHeartBeatClientHandler(String clientId, String userName, String password) {
		this.clientId = clientId;
		this.userName = userName;
		this.password = password.getBytes();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// discard all messages
		ReferenceCountUtil.release(msg);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		MqttFixedHeader connectFixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
		MqttConnectVariableHeader connectVariableHeader = new MqttConnectVariableHeader(PROTOCOL_NAME_MQTT_3_1_1, PROTOCOL_VERSION_MQTT_3_1_1, true, true, false, 0, false, false, 20);
		MqttConnectPayload connectPayload = new MqttConnectPayload(clientId, null, null, userName, password);
		MqttConnectMessage connectMessage = new MqttConnectMessage(connectFixedHeader, connectVariableHeader, connectPayload);
		ctx.writeAndFlush(connectMessage);
		log.info("channelActive:{}");
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			MqttFixedHeader pingreqFixedHeader = new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0);
			MqttMessage pingreqMessage = new MqttMessage(pingreqFixedHeader);
			ctx.writeAndFlush(pingreqMessage);
			System.out.println("Sent PINGREQ");
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
