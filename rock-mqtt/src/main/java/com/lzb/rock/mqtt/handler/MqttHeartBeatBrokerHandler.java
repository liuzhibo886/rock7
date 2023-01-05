
package com.lzb.rock.mqtt.handler;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lzb.rock.base.holder.SpringContextHolder;
import com.lzb.rock.base.util.UtilString;
import com.lzb.rock.mqtt.context.MyNettyContext;
import com.lzb.rock.mqtt.mapper.ClientMapper;
import com.lzb.rock.mqtt.model.Client;
import com.lzb.rock.mqtt.service.IMqttReadService;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * https://www.cnblogs.com/sanshengshui/p/9826009.html MQTT.fx
 * 
 * @author LZB
 *
 */
@Sharable
//@Scope("prototype")
@Slf4j
@Component
public class MqttHeartBeatBrokerHandler extends SimpleChannelInboundHandler<MqttMessage> {

	@Autowired
	ClientMapper clientMapper;

	@Override
	public void channelRead0(ChannelHandlerContext ctx, MqttMessage mqttMessage) throws Exception {
	//	log.info("收到消息,messageType:{}:{}", mqttMessage.fixedHeader().messageType(), mqttMessage);

//		IMqttReadService callBack=applicationContext.getBean(mqttMessage.fixedHeader().messageType().name(),IMqttReadService.class);
		IMqttReadService callBack = SpringContextHolder.getBean(mqttMessage.fixedHeader().messageType().name());

		if (callBack == null) {
			log.warn("未知类型消息:{}", mqttMessage.fixedHeader().messageType());
			ReferenceCountUtil.release(mqttMessage);
			ctx.close();
			return;
		}
		callBack.run(ctx.channel(), mqttMessage);

//		switch (mqttMessage.fixedHeader().messageType()) {
//		case CONNECT:
//			// 建立新连接
//			break;
//		case PUBLISH:
//			// 发布消息
//			break;
//		case PUBACK:
//			// 收到发布消息确认 qos1
//			break;
//		case PUBREC:
//			// 收到发布消息确认 qos2
//			break;
//		case PUBREL:
//			// 收到发布消息确认 qos2
//			break;
//		case PUBCOMP:
//			// 收到发布消息确认 qos2
//			break;
//		case CONNACK:
//			// 服务端到客户端 ,连接报文确认
//			break;
//		case PINGREQ:
//			// 心跳
//			break;
//		case PINGRESP:
//			// 心跳响应
//			break;
//		case DISCONNECT:
//			break;
//		case SUBSCRIBE:
//			break;
//		case SUBACK:
//			break;
//		case UNSUBSCRIBE:
//			log.info("收到取消订阅消息{}", mqttMessage);
//			break;
//		default:
//			log.warn("未知类型消息:{}", mqttMessage.fixedHeader().messageType());
//			ReferenceCountUtil.release(mqttMessage);
//			ctx.close();
//			break;
//		}

//	case PUBACK:
//		protocolProcess.pubAck().processPubAck(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
//		break;
//	case PUBREC:
//		protocolProcess.pubRec().processPubRec(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
//		break;
//	case PUBREL:
//		protocolProcess.pubRel().processPubRel(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
//		break;
//	case PUBCOMP:
//		protocolProcess.pubComp().processPubComp(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
//		break;
//	case SUBSCRIBE:
//		protocolProcess.subscribe().processSubscribe(ctx.channel(), (MqttSubscribeMessage) msg);
//		break;
//	case SUBACK:
//		break;
//	case UNSUBSCRIBE:
//		protocolProcess.unSubscribe().processUnSubscribe(ctx.channel(), (MqttUnsubscribeMessage) msg);
//		break;
//	case UNSUBACK:
//		break;
//	case PINGREQ:
//		protocolProcess.pingReq().processPingReq(ctx.channel(), msg);
//		break;
//	case PINGRESP:
//		break;
//	case DISCONNECT:
//		protocolProcess.disConnect().processDisConnect(ctx.channel(), msg);
//		break;
//	default:
//		break;

	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

//		MqttFixedHeader pingreqFixedHeader = new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE,
//				false, 0);
//		MqttMessage pingResp = new MqttMessage(pingreqFixedHeader);
//		ctx.writeAndFlush(pingResp);

		if (evt instanceof IdleStateEvent && IdleState.READER_IDLE == ((IdleStateEvent) evt).state()) {
			String clientId = ctx.channel().attr(MyNettyContext.CLIENT_ID_KEY).get();
			if (UtilString.isNotBlank(clientId)) {
				Client client = new Client();
				client.setClientId(clientId);
				client.setStatus(3);
				client.setLastTime(new Date());
				clientMapper.upsertByClientId(client);
				
			}
			MyNettyContext.disConnect(ctx.channel());
			log.info("超时，断开连接：{}", clientId);
			ctx.close();
		}
	}

	/**
	 * 当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

		String clientId = ctx.channel().attr(MyNettyContext.CLIENT_ID_KEY).get();
		if (UtilString.isNotBlank(clientId)) {
			Client client = new Client();
			client.setClientId(clientId);
			client.setStatus(2);
			client.setLastTime(new Date());
			clientMapper.upsertByClientId(client);
		}
		MyNettyContext.disConnect(ctx.channel());
		log.info("exceptionCaught 断开：{}", clientId);

		cause.printStackTrace();
		ctx.close();
	}
}
