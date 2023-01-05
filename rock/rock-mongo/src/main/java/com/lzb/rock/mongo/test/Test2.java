package com.lzb.rock.mongo.test;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.mongodb.ConnectionString;

import lombok.extern.slf4j.Slf4j;

/**
 * https://blog.csdn.net/wmq880204/article/details/114294947
 * 
 * @author lzb
 *
 */
@Slf4j
public class Test2 {

	public static void main(String[] args) {

		String url = "mongodb://tt_dev:AiJvXvMpW67GmFYP@8.218.112.32:27017/tt_dev";

		SimpleMongoClientDatabaseFactory mongoDbFactory = new SimpleMongoClientDatabaseFactory(
				new ConnectionString(url));
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory);

//		List<AggregationOperation> operations = new ArrayList<>();
//		operations.add(Aggregation.match(Criteria.where("taskId").is(254)));
//		operations.add(Aggregation.group("map.uid"));
//
//		Aggregation aggregation = Aggregation.newAggregation(operations);
//		AggregationResults<ManageLog> results = mongoTemplate.aggregate(aggregation, "manageLog", ManageLog.class);
//		List<ManageLog> list = results.getMappedResults();
//		for (ManageLog manageLog : list) {
//			//log.info("==>{}", UtilJson.getStr(manageLog));
//		}



	}

}
