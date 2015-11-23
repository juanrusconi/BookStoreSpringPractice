package controller;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import model.Person;


public interface PersonRepository extends MongoRepository<Person, String> {

	public Person findByName (String name);
	
	@Query("{ 'name': '?0' }")
//	@Query("db.person.find({ name : { $is: ?0 }}")
	public boolean existsByName (String name);
	
}