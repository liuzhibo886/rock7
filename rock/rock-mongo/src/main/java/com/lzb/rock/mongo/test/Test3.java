package com.lzb.rock.mongo.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
public class Test3 {

	public static void main(String[] args) {
		String url = "mongodb://tt_dev:AiJvXvMpW67GmFYP@8.218.112.32:27017/tt_dev";

		SimpleMongoClientDatabaseFactory mongoDbFactory = new SimpleMongoClientDatabaseFactory(
				new ConnectionString(url));
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory);

		Integer count = 0; // 执行次数
		Map<String, Integer> map = new HashMap<String, Integer>();

		Criteria criteria = Criteria.where("taskId").is(279).and("name").is("SendMessageTo");
		Query query = new Query(criteria);
		List<String> uids = mongoTemplate.findDistinct(query, "map.uid", ManageLog.class, String.class);
		
		for (String uid : uids) {
			Criteria criteria2 = Criteria.where("taskId").is(279).and("name").is("SendMessageTo").and("map.uid")
					.is(uid);
			Query query2 = new Query(criteria2);
			List<ManageLog> list2 = mongoTemplate.find(query2, ManageLog.class, "manageLog");

			Integer successCount = 0;
			
			Set<String> set=new HashSet<String>();
			for (ManageLog manageLog : list2) {
				CallBackDto dto = manageLog.getCallBackDto();
				if (dto == null) {
					continue;
				}
				Integer ret = dto.getRet();
				String msg = dto.getMsg();
				if (ret == 0) {
					successCount++;
				}
				String key = "_";
				if (UtilJson.isJsonString(msg)) {
					JSONObject msgObj = UtilJson.getJsonObject(msg);
					String tips = msgObj.getJSONObject("status_msg").getJSONObject("msg_content").getString("tips");
					key = ret + "_" + tips;
				} else {
					key = ret + "_" + msg;
				}
				set.add(key);
			}
			String mapKey="";
			for (String key : set) {
				mapKey=mapKey+"_"+key;
			}
			mapKey=mapKey.substring(1);
			Integer obj = map.get(mapKey);
			if (obj == null) {
				obj = 0;
			}
			map.put(mapKey, ++obj);
			count++;
		}


//		for (Entry<String, Integer> manageLog : map.entrySet()) {
//			log.info("key:{};value:{}", manageLog.getKey(), manageLog.getValue());
//		}
//		log.info("count:{}", count);

		for (Entry<String, Integer> manageLog : map.entrySet()) {
			System.out.println("key:" + manageLog.getKey() + ";value:" + manageLog.getValue());
		}
		System.out.println("count:" + count);
		System.out.println("size:" + map.size());

	}

}
