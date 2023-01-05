package com.lzb.rock.mqtt.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.lzb.rock.mqtt.model.SendMsg;
import com.mongodb.ConnectionString;

public class TestMongodbSendMsg {

	public static void main(String[] args) {

		String url = "mongodb://mqtt:MMItimkUqkQStcG7@82.157.167.82:27017/mqtt?authSource=mqtt";
		// String url =
		// "mongodb://mqtt:MMItimkUqkQStcG7@8.218.112.32:27017/mqtt?sockettimeoutms=600000&waitqueuetimeoutms=600000&connecttimeoutms=600000";

		ConnectionString connectionString = new ConnectionString(url);

		SimpleMongoClientDatabaseFactory mongoDbFactory = new SimpleMongoClientDatabaseFactory(connectionString);
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory);

		Criteria criteria = Criteria.where("clientId").is("lzb_test_0");
		// Query query = new Query(criteria);
		Query query = new Query();

		// query.limit(1000);

		List<SendMsg> findIterable = mongoTemplate.find(query, SendMsg.class, "sendMsg");
		Set<String> set = new HashSet<String>();
		AtomicInteger count = new AtomicInteger(0);
		for (SendMsg sendMsg : findIterable) {
			count.getAndIncrement();
			if (sendMsg.getOldId() == null) {
				System.out.println(sendMsg.getClientId());
			}
			String str=sendMsg.getClientId()+"#"+sendMsg.get_id().toHexString();
			if(set.contains(str)) {
				System.out.println(sendMsg.getClientId());
			}
			set.add(str);
			
		}

		System.out.println(count.intValue());
	}
}
