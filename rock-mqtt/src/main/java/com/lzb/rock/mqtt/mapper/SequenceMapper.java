package com.lzb.rock.mqtt.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.lzb.rock.mqtt.model.Sequence;

@Component
public class SequenceMapper {

	Object indexLock = new Object();

	@Autowired
	MongoTemplate mongoTemplate;

	public Long nextId(String clientId, String type) {

		Criteria criteria = Criteria.where("clientId").is(clientId).and("type").is(type);
		Query query = new Query(criteria);
		Update update = new Update();
		update.inc("index", 1L);
		FindAndModifyOptions options = new FindAndModifyOptions();
		options.returnNew(true);
		options.upsert(true);
		Sequence sequence = mongoTemplate.findAndModify(query, update, options, Sequence.class);

		return sequence.getIndex();

	}
}
