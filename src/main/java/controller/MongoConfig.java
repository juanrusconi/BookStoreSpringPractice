package controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {

	private final String DB_NAME = "bookstoredb";
	private final String DB_ADRESS = "127.0.0.1:27017";
//	private final String DB_USER = "user";
//	private final String DB_PASS = "1234";
	
	@Override
	public String getDatabaseName() {
		return DB_NAME;
	}

	@Override
	public Mongo mongo(){
		return new MongoClient(DB_ADRESS);
	}

}
