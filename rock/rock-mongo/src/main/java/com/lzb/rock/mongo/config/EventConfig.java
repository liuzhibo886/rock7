package com.lzb.rock.mongo.config;
//package com.lzb.rock.mongo.config;
//
//import java.util.List;
//
//import org.apache.http.conn.util.DomainType;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.event.EventListener;
//import org.springframework.data.mapping.context.MappingContext;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.index.IndexOperations;
//import org.springframework.data.mongodb.core.index.IndexResolver;
//import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
//import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
//import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
//
//import lombok.RequiredArgsConstructor;
//
//@RequiredArgsConstructor
//@Configuration
//public class EventConfig {
//
//	@Autowired
//	List<MongoTemplate> mongoTemplates;
//
//	@Autowired
//	MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mongoMappingContext;
//
//	@EventListener(ApplicationReadyEvent.class)
//	public void initIndicesAfterStartup() {
//
//		for (MongoTemplate mongoTemplate : mongoTemplates) {
//			IndexOperations indexOps = mongoTemplate.indexOps(DomainType.class);
//			IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
//			resolver.resolveIndexFor(DomainType.class).forEach(indexOps::ensureIndex);
//		}
//
//	}
//}
