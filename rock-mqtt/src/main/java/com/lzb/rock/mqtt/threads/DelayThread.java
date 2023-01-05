package com.lzb.rock.mqtt.threads;

import org.bson.types.ObjectId;

import com.lzb.rock.base.holder.SpringContextHolder;
import com.lzb.rock.base.model.MyDelayed;
import com.lzb.rock.base.util.UtilJson;
import com.lzb.rock.mqtt.context.DelayContext;
import com.lzb.rock.mqtt.context.MyNettyContext;
import com.lzb.rock.mqtt.mapper.SendMsgMapper;
import com.lzb.rock.mqtt.model.SendMsg;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DelayThread extends Thread {

	@Override
	public void run() {
		while (true) {
			MyDelayed<ObjectId> myDelayed = DelayContext.take();
			SendMsgMapper sendMsgMapper = SpringContextHolder.getBean(SendMsgMapper.class);
			SendMsg msg = sendMsgMapper.findById(myDelayed.getData());
			if (msg != null && msg.getAck() == MqttMessageType.PUBLISH.value() && msg.getQos() == 1) {
				Channel channel = MyNettyContext.getChannel(msg.getClientId());
				if (channel == null) {
					log.info("用户已经离线：{}", UtilJson.getStr(msg));
					continue;
				}
				sendMsgMapper.incCount(msg.get_id());
				MyNettyContext.send(channel, msg.getTopic(), msg.getQos(), msg.getPayload().getBytes(),
						msg.getPacketId());

				if (msg.getCount() < 5) {
					// 延时检测，未收到ack 重试
					myDelayed.rest();
					DelayContext.offer(myDelayed);
				}

			}

		}

	}

}
