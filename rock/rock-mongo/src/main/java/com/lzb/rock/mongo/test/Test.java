package com.lzb.rock.mongo.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.alibaba.fastjson.JSONObject;
import com.lzb.rock.base.util.UtilJson;
import com.lzb.rock.mongo.test.model.CallBackDto;
import com.lzb.rock.mongo.test.model.ManageLog;
import com.mongodb.ConnectionString;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test {

	public static void main(String[] args) {
		String url = "mongodb://tt_dev:AiJvXvMpW67GmFYP@8.218.112.32:27017/tt_dev";

		SimpleMongoClientDatabaseFactory mongoDbFactory = new SimpleMongoClientDatabaseFactory(
				new ConnectionString(url));
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory);

		Criteria criteria = Criteria.where("taskId").is(279).and("name").is("SendMessageTo");
		Query query = new Query(criteria);

		List<ManageLog> list = mongoTemplate.find(query, ManageLog.class, "manageLog");
		Integer count = 0; // 执行次数
		Map<String, Integer> map = new HashMap<String, Integer>();

		for (ManageLog manageLog : list) {
			String key = "";
			count++;
			CallBackDto dto = manageLog.getCallBackDto();
			if (dto == null) {
				key = "_";
			}

			if (dto != null) {
				Integer ret = dto.getRet();
				String msg = dto.getMsg();
				if (UtilJson.isJsonString(msg)) {
					JSONObject msgObj = UtilJson.getJsonObject(msg);
					String tips = msgObj.getJSONObject("status_msg").getJSONObject("msg_content").getString("tips");
					key = ret + "_" + tips;
				} else {
					key = ret + "_" + msg;
				}
			}
			Integer obj = map.get(key);
			if (obj == null) {
				obj = 0;
			}

			map.put(key, ++obj);
		}

//		for (Entry<String, Integer> manageLog : map.entrySet()) {
//			log.info("key:{};value:{}", manageLog.getKey(), manageLog.getValue());
//		}
//		log.info("count:{}", count);

		for (Entry<String, Integer> manageLog : map.entrySet()) {
			System.out.println("key:" + manageLog.getKey() + ";value:" + manageLog.getValue());
		}
		System.out.println("count:" + count);

	}

}
