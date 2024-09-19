package com.thepapiok.multiplecard.configs;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.SessionSynchronization;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class DbConfig extends AbstractMongoClientConfiguration {
  @Value("${spring.data.mongodb.database}")
  private String name;

  @Value("${spring.data.mongodb.uri}")
  private String mongoUri;

  @Bean
  MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
    return new MongoTransactionManager(dbFactory);
  }

  @Override
  public MongoClient mongoClient() {
    return MongoClients.create(mongoUri);
  }

  @Bean
  public MongoTemplate mongoTemplate() {
    MongoTemplate mongoTemplate = new MongoTemplate(mongoClient(), getDatabaseName());
    mongoTemplate.setSessionSynchronization(SessionSynchronization.ALWAYS);
    return mongoTemplate;
  }

  @Override
  protected String getDatabaseName() {
    return name;
  }
}
