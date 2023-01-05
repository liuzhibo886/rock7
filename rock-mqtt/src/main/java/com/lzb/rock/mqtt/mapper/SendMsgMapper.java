package com.lzb.rock.mqtt.mapper;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.lzb.rock.mqtt.model.SendMsg;

@Component
public class SendMsgMapper {

	@Autowired
	MongoTemplate mongoTemplate;

	public void insert(SendMsg sendMsg) {
		sendMsg.setLastTime(new Date());
		mongoTemplate.insert(sendMsg);
	}

	public void updateMultiAck(String clientId, Integer packetId, Integer oldAck, Integer ack) {
		Criteria criteria = Criteria.where("clientId").is(clientId).and("packetId").is(packetId).and("ack").is(oldAck);
		Query query = new Query(criteria);

		Update update = new Update();
		update.set("ack", ack);
		update.set("lastTime", new Date());
		update.inc("ackCount",1);

		mongoTemplate.updateMulti(query, update, SendMsg.class);
	}

	public SendMsg findById(ObjectId _id) {

		SendMsg sendMsg = mongoTemplate.findById(_id, SendMsg.class);

		return sendMsg;
	}
	

	public List<SendMsg> findByClientIdAndAck(String clientId, Integer ack) {

		Criteria criteria = Criteria.where("clientId").is(clientId).and("ack").is(ack);
		Query query = new Query(criteria);

		 List<SendMsg> sendMsgs = mongoTemplate.find(query, SendMsg.class);

		return sendMsgs;
	}

	public void incCount(ObjectId _id) {
		Criteria criteria = Criteria.where("_id").is(_id);
		Query query = new Query(criteria);

		Update update = new Update();
		update.inc("count", 1);
		update.set("lastTime", new Date());

		mongoTemplate.upsert(query, update, SendMsg.class);
	}
}
