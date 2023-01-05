package com.lzb.rock.rocketmq.maper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.lzb.rock.base.enums.ResultEnum;
import com.lzb.rock.base.exception.BusException;
import com.lzb.rock.base.facade.IMqDelayLevelEnum;
import com.lzb.rock.base.facade.IMqEnum;
import com.lzb.rock.base.facade.IMqMapper;
import com.lzb.rock.base.model.DelayDelMq;
import com.lzb.rock.base.model.DelayMq;
import com.lzb.rock.base.util.UtilJson;
import com.lzb.rock.base.util.UtilString;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RocketMqMapper implements IMqMapper {

	@Autowired
	@Qualifier("defaultMQProducer")
	private DefaultMQProducer producer;

	@Override
	public String sendMsg(String body, IMqEnum mqEnum) {

		return sendMsg(body, mqEnum.getTopic(), mqEnum.getTag());
	}

	@Override
	public String sendMsg(String body, String topic, String tag) {

		if (UtilString.isBlank(body)) {
			throw new BusException(ResultEnum.PAEAM_ERR, "消息体不能为空");
		}
		if (UtilString.isBlank(topic)) {
			throw new BusException(ResultEnum.PAEAM_ERR, "topic不能为空");
		}

		if (UtilString.isBlank(tag)) {
			throw new BusException(ResultEnum.PAEAM_ERR, "tag不能为空");
		}

		Message message = new Message(topic, tag, UtilString.getBytes(body));
		try {
			SendResult rs = producer.send(message);
			// sendResult != null && sendResult.getSendStatus() == SendStatus.SEND_OK;
			return rs.getMsgId();
		} catch (Exception e) {
			log.error("sendMsg消息发送失败;topic:{};tag:{};body:{};ex:{}", topic, tag, body, e);
			e.printStackTrace();
		}
		// sendResult != null && sendResult.getSendStatus() == SendStatus.SEND_OK;
		return null;

	}

	public void sendMsg(List<Message> messages) {
		if (messages.size() > 0) {
			List<Message> messagesNew = new ArrayList<Message>(200);
			Integer count = 0;
			for (Message message : messages) {
				messagesNew.add(message);
				count++;
				if (count > 199) {
					try {
						producer.send(messages);
						messagesNew = new ArrayList<Message>(200);
						count = 0;
					} catch (Exception e) {
						log.error("sendMsg消息发送失败;messages:{}", messages);
						e.printStackTrace();
					}
				}
			}
			try {
				if (messagesNew.size() > 0) {
					producer.send(messagesNew);
				}
			} catch (Exception e) {
				log.error("sendMsg消息发送失败;messages:{}", messagesNew);
				e.printStackTrace();
			}
		}

	}

	@Override
	public void sendMsg(List<String> bodys, IMqEnum mqEnum) {
		if (bodys == null || bodys.size() < 1) {
			return;
		}
		if (mqEnum == null) {
			return;
		}

		List<Message> messages = new ArrayList<Message>(100);
		Integer count = 1;
		for (String body : bodys) {
			Message message = new Message(mqEnum.getTopic(), mqEnum.getTag(), UtilString.getBytes(body));
			messages.add(message);
			count++;
			if (count > 100) {
				try {
					producer.send(messages);
				} catch (Exception e) {
					log.error("sendMsg消息发送失败;topic:{};tag:{};bodys:{};ex:{}", mqEnum.getTopic(), mqEnum.getTag(), bodys,
							e);
					e.printStackTrace();
				}
				count = 0;
				messages = new ArrayList<Message>(100);
			}
		}
		if (messages.size() > 0) {
			try {
				producer.send(messages);
			} catch (Exception e) {
				log.error("sendMsg消息发送失败;topic:{};tag:{};bodys:{};ex:{}", mqEnum.getTopic(), mqEnum.getTag(), bodys, e);
				e.printStackTrace();
			}
		}

	}

	@Override
	public void sendOneWayMsg(String body, IMqEnum mqEnum) {
		Message message = new Message(mqEnum.getTopic(), mqEnum.getTag(), UtilString.getBytes(body));
		try {
			producer.sendOneway(message);
		} catch (Exception e) {
			log.error("sendOneWayMsg消息发送失败;topic:{};tag:{};body:{};ex:{}", mqEnum.getTopic(), mqEnum.getTag(), body, e);
			e.printStackTrace();
		}
	}

	@Override
	public String sendDelayMsg(String body, IMqEnum mqEnum, Long delayTime) {
		DelayMq delay = new DelayMq();
		delay.setTopic(mqEnum.getTopic());
		delay.setTag(mqEnum.getTag());
		delay.setMessage(body);
		delay.setDelayTime(delayTime);
		return sendMsg(UtilJson.getStr(delay), "DELAY", "DELAY");
	}

	@Override
	public String sendDelayMsg(String body, IMqEnum mqEnum, IMqDelayLevelEnum delayLevel) {
		Message message = new Message(mqEnum.getTopic(), mqEnum.getTag(), UtilString.getBytes(body));
		message.setDelayTimeLevel(delayLevel.getLevel());
		try {
			SendResult sendResult = producer.send(message);
			return sendResult.getMsgId();
		} catch (Exception e) {
			log.error("sendDelayMsg消息发送失败;topic:{};tag:{};body:{};ex:{}", mqEnum.getTopic(), mqEnum.getTag(), body, e);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void destroy() {
		if (Objects.nonNull(producer)) {
			producer.shutdown();
		}
	}

	@Override
	public String delMsg(String msgId, ObjectId delayId) {

		if (UtilString.isBlank(msgId) && delayId == null) {
			log.error("msgId 跟delayId 不能都为空");
			return null;
		}
		DelayDelMq delayDelMq = new DelayDelMq();
		delayDelMq.setDelayId(delayId);
		delayDelMq.setMsgId(msgId);

		return sendMsg(UtilJson.getStr(delayDelMq), "DELAY_DEL", "DELAY_DEL");
	}

}
