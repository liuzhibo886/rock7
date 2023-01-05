package com.lzb.rock.mqtt.mapper;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.lzb.rock.mqtt.model.PubMsg;

@Component
public class PubMsgMapper {

	@Autowired
	MongoTemplate mongoTemplate;

	public void insert(PubMsg pubMsg) {

		mongoTemplate.insert(pubMsg);
	}
	
	public void updateMultiAck(String clientId, Integer packetId, Integer oldAck, Integer ack) {
		Criteria criteria = Criteria.where("clientId").is(clientId).and("packetId").is(packetId).and("ack").is(oldAck);
		Query query = new Query(criteria);

		Update update = new Update();
		update.set("ack", ack);
		update.inc("ackCount",1);

		update.set("lastTime", new Date());

		mongoTemplate.updateMulti(query, update, PubMsg.class);
	}
}
