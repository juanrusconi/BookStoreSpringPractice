package controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
/* ---- Spring annotations ---- */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/* --- Exception classes --- */
import exception.CollectionIsEmptyException;
import exception.PersonAlreadyExistsException;
import exception.PersonDoesNotExistsException;

import model.Book;
import model.MyLink;
import model.Person;;

@RestController
@RequestMapping("/persons")
public class PersonController {
	
	@Autowired
	private PersonRepository personRepo;
	
	private MongoTemplate mongoTemp;
	
	
	
	@RequestMapping(method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Person>> getAllPersons() throws CollectionIsEmptyException{
		List<Person> collection = personRepo.findAll();
		if (!collection.isEmpty())
			return new ResponseEntity<List<Person>>(collection, HttpStatus.OK);			
		throw new CollectionIsEmptyException(mongoTemp.getCollectionName(Person.class));
	}
	
	
	
	@RequestMapping(value="/{personName}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Person> getPerson(@PathVariable("personName") String name) throws PersonDoesNotExistsException{
		if (personRepo.findByName(name)!=null) 
			return new ResponseEntity<Person>(personRepo.findByName(name), HttpStatus.OK);
		throw new PersonDoesNotExistsException(name);
	}
	
	
	
	@RequestMapping(method=RequestMethod.POST)
	ResponseEntity<Void> createPerson (@RequestParam(value="name", defaultValue="no_name") String name) 
												throws URISyntaxException, PersonAlreadyExistsException{
		
		Person newPerson = new Person(name, new ArrayList<Book>(), new ArrayList<MyLink>());
		newPerson.addLink(new MyLink("self", (ControllerLinkBuilder.linkTo(PersonController.class)).toString()+newPerson.getName()));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setLocation(new URI((ControllerLinkBuilder.linkTo(PersonController.class)).toString()+newPerson.getName()));
		
		if (personRepo.findByName(newPerson.getName())==null){
			HttpStatus operationStatus = (personRepo.save(newPerson)!=null)? HttpStatus.OK:HttpStatus.BAD_REQUEST;
			return new ResponseEntity<Void>(headers, operationStatus);
		}
		throw new PersonAlreadyExistsException(newPerson.getName());
	}
}
