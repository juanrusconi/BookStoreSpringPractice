package repository;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.util.JSON;

@Resource(name="mongoTemplate")
public class MongoService{
	/*
	 * Controllers will use this repository which handles DB transactions 
	 */
	
	private ApplicationContext context = new AnnotationConfigApplicationContext(MongoConfig.class);
	private MongoTemplate mongoTemplate = new MongoTemplate(context.getBean(MongoConfig.class).mongo(), context.getBean(MongoConfig.class).getDatabaseName());
	
	
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
	
	
	
	public Object getDoc(String id, Class<?> entityClass){
//		return mongoTemplate.findById(id, entityClass, mongoTemplate.getCollectionName(entityClass));
//		System.out.println("result : "+mongoTemplate.getCollection(mongoTemplate.getCollectionName(entityClass)).findOne(id));
//		System.out.println(mongoTemplate.find(new BasicQuery("{_id:"+id+"}"), entityClass).toString());
//		System.out.println(mongoTemplate.findOne(new Query(Criteria.where("_id").is(id)), entityClass).toString());
		return mongoTemplate.findById(id, entityClass, mongoTemplate.getCollectionName(entityClass));
		
	}
	
	
	
	public String getColl(Class<?> entityClass){
		/*
		 * returns the collection (related to the given entity class) in its JSON form 
		 */
		return JSON.serialize(mongoTemplate.getCollection(mongoTemplate.getCollectionName(entityClass)).find());
	}
	
	
	
	//TODO: deleteDoc()
}
