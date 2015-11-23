package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import exception.CollectionIsEmptyException;
import exception.PersonDoesNotExistsException;
import model.Person;
import model.Book;
import model.MyLink;;

@RestController
@RequestMapping("/persons")
public class PersonController {
	
	@Autowired
	PersonRepository personRepo;
	
	public static String person_uri = "http://localhost:8080/persons/";
	private final AtomicLong counter = new AtomicLong();
	private MongoTemplate mongoTemp;
	
	
	
	@RequestMapping(method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Person>> getAllPersons() throws CollectionIsEmptyException{
		List<Person> collection = personRepo.findAll();
		if (!collection.isEmpty())
			return new ResponseEntity<List<Person>>(collection, HttpStatus.OK);			
		throw new CollectionIsEmptyException(mongoTemp.getCollectionName(Person.class));
	}
	
	
	
	@RequestMapping(method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Person> getPerson(@RequestParam(value="fname", defaultValue="nofirst") String fname,
									@RequestParam(value="lname", defaultValue="nolast") String lname) 
																			throws PersonDoesNotExistsException{
		if (personRepo.existsByFullName(fname, lname)) 
			return new ResponseEntity<Person>(personRepo.findByFullName(fname, lname), HttpStatus.OK);
		throw new PersonDoesNotExistsException(fname, lname);
	}
	
	
	
	@RequestMapping(method=RequestMethod.POST)
	ResponseEntity<Void> createPerson (@RequestParam(value="fname", defaultValue="nofirst") String fname, 
										@RequestParam(value="lname", defaultValue="nolast") String lname){
		Person newPerson = new Person(fname, lname, new ArrayList<Book>(), new ArrayList<MyLink>());
		
	}
}
