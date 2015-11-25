package controller;

import org.springframework.data.mongodb.repository.MongoRepository;
import model.Person;

public interface PersonRepository extends MongoRepository<Person, String> {

	public Person findByName (String name);
	public void deleteByName (String name);
//	public boolean exists(String name);
}