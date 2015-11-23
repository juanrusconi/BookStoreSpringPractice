package controller;

import org.springframework.data.mongodb.repository.MongoRepository;

import model.Person;

public interface PersonRepository extends MongoRepository<Person, String> {

	public Person findByFullName (String fname, String lname);
	public boolean existsByFullName (String fname, String lname);
	
}
