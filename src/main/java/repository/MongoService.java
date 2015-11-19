package repository;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.mongodb.util.JSON;

public class MongoService {
	/*
	 * Controllers will use this repository which handles DB transactions 
	 */
	
	ApplicationContext context = new AnnotationConfigApplicationContext(MongoConfig.class);
	MongoTemplate mongoTemplate = new MongoTemplate(context.getBean(MongoConfig.class).mongo(), context.getBean(MongoConfig.class).getDatabaseName());
	
	public boolean createDoc(Object newObj){
		try {
			mongoTemplate.save(newObj); //the id will be generated automatically since the model object has the @Id annotation
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean existingDoc(Object id, Class<?> entityClass){
		return mongoTemplate.exists(new Query(Criteria.where("id").is(id)), entityClass);
	}
	
	public String getDoc(Object id, Class<?> entityClass){
		return JSON.serialize(mongoTemplate.find(new Query(Criteria.where("id").is(id)), entityClass));
	}
	
	public String getColl(Class<?> entityClass){
		/*
		 * returns the collection (related to the given entity class) in its JSON form 
		 */
		return JSON.serialize(mongoTemplate.getCollection(mongoTemplate.getCollectionName(entityClass)).find());
	}
	
	//TODO: deleteDoc()
}
