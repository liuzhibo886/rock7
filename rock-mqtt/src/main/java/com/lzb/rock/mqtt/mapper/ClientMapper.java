package com.lzb.rock.mqtt.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.lzb.rock.mongo.util.UtilMongoUpdate;
import com.lzb.rock.mqtt.model.Client;

@Component
public class ClientMapper {

	@Autowired
	MongoTemplate mongoTemplate;

	public void upsertByClientId(Client client) {
		Update update = UtilMongoUpdate.set(client, "_id", "clientId");
		Criteria criteria = Criteria.where("clientId").is(client.getClientId());
		Query query = new Query(criteria);
		mongoTemplate.upsert(query, update, Client.class);
	}

	public Client findByClientId(String clientId) {
		Criteria criteria = Criteria.where("clientId").is(clientId);
		Query query = new Query(criteria); 
		Client client = mongoTemplate.findOne(query, Client.class);
		return client;
	}
}
