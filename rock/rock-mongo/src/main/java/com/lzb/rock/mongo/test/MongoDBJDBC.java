package com.lzb.rock.mongo.test;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBJDBC {

	public static void main(String[] args) {

		 
		// String url="mongodb://user:pass@ip:port/admin?authSource=admin&authMechanism=SCRAM-SHA-1";
		String url = "mongodb://tt_loc:AiJvXvMpW67GmFYP@8.218.112.32:27017/";
		MongoClient mongoClient = MongoClients.create(url);

		// 连接到 mongodb 服务
		MongoDatabase mongoDatabase = mongoClient.getDatabase("lzb_test");

		mongoDatabase.createCollection("test");
		System.out.println("集合创建成功");

		MongoCollection<Document> collection = mongoDatabase.getCollection("test");

		System.out.println("集合 test 选择成功");

		Document document = new Document("title", "MongoDB").append("description", "database").append("likes", 100)
				.append("by", "Fly");

		List<Document> documents = new ArrayList<Document>();
		documents.add(document);
		collection.insertMany(documents);

		mongoClient.close();
	}
}
