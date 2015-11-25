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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import exception.BookAlreadyExistsException;
import exception.BookDoesNotExistException;
/* --- Exception classes --- */
import exception.CollectionIsEmptyException;
import exception.PersonAlreadyExistsException;
import exception.PersonDoesNotExistsException;

import model.Book;
import model.MyLink;
import model.Person;;

@RestController
@RequestMapping("bookstore/persons")
public class PersonController {
	
	@Autowired
	PersonRepository personRepo;
	BookRepository bookRepo;
	MongoTemplate mongoTemp;
	
	private BookController bookCont = new BookController(bookRepo);
	
	
	
	@RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Person>> getAllPersons() throws CollectionIsEmptyException{
		List<Person> collection = personRepo.findAll();
		if (!collection.isEmpty())
			return new ResponseEntity<List<Person>>(collection, HttpStatus.OK);			
		throw new CollectionIsEmptyException(mongoTemp.getCollectionName(Person.class));
	}
	
	
	
	@RequestMapping(value="/{personName}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Person> getPerson(@PathVariable("personName") String name) throws PersonDoesNotExistsException{
		if (personRepo.findByName(name)!=null) 
			return new ResponseEntity<Person>(personRepo.findByName(name), HttpStatus.OK);
		throw new PersonDoesNotExistsException(name);
	}
	
	
	
	@RequestMapping(method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> createPerson (@RequestParam(value="name", defaultValue="no_name") String name) 
												throws URISyntaxException, PersonAlreadyExistsException{
		
		Person newPerson = new Person(name, new ArrayList<Book>(), new ArrayList<MyLink>());
		newPerson.addLink(new MyLink(MyLink.REL_SELF, getUriForPersons(newPerson.getName()).toString()));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setLocation(new URI((ControllerLinkBuilder.linkTo(PersonController.class)).toString()+newPerson.getName()));
		
		if (personRepo.findByName(newPerson.getName())==null){
			HttpStatus operationStatus = (personRepo.save(newPerson)!=null)? HttpStatus.CREATED:HttpStatus.BAD_REQUEST;
			return new ResponseEntity<Void>(headers, operationStatus);
		}
		throw new PersonAlreadyExistsException(newPerson.getName());
	}
	
	
	
	@RequestMapping(method=RequestMethod.DELETE, value = "/{personName}", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deletePerson(@PathVariable String personName ) throws PersonDoesNotExistsException, URISyntaxException{
		
		if (personRepo.findByName(personName)!=null){
			personRepo.deleteByName(personName);			
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation((getUriForPersons(""))); //returns the URI of the collection it was in
			
			return new ResponseEntity<Void>(headers, HttpStatus.OK);
		}
		throw new PersonDoesNotExistsException(personName);
	}
	
	
	// ------------------------------------- person's books (reservations) methods ------------------------------------------------
	
	
	
	@RequestMapping(method=RequestMethod.GET, value = "/{personName}/books", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Book>> getAllPersonBooks(@PathVariable String personName) throws PersonDoesNotExistsException{
		/*
		 * returns the list of books from the given person
		 */
		return new ResponseEntity<List<Book>>(getPerson(personName).getBody().getBooks(), HttpStatus.OK);
	}
	
	
	
	@RequestMapping(method=RequestMethod.GET, value = "/{personName}/books/{bookId}", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Book> getPersonBook(@PathVariable String personName, 
												@PathVariable String bookId) throws PersonDoesNotExistsException, BookDoesNotExistException{
		/*
		 * returns an existing book from the given person
		 */
		Person person = getPerson(personName).getBody();
		if (person == null) throw new PersonDoesNotExistsException(personName);
		Book book = person.findBook(bookId);
		if ( book == null) throw new BookDoesNotExistException(bookId);
		
		return new ResponseEntity<Book>(book, HttpStatus.OK);		
	}
	
	
	
	@RequestMapping(method=RequestMethod.POST, value = "/{personName}/books/{bookId}", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> addPersonsBook (@PathVariable String personName, @PathVariable String bookId)
			throws PersonDoesNotExistsException, BookAlreadyExistsException, BookDoesNotExistException, URISyntaxException{
		/*
		 * adds the given book to the specified person's book list, previously checking that the book exists.
		 * Returns the location of the newly created resource.
		 */
		Person person = getPerson(personName).getBody();
		if (person == null) throw new PersonDoesNotExistsException(personName);
		if (person.findBook(bookId) != null) throw new BookAlreadyExistsException(bookId);
		Book newBorrowedBook = bookCont.getBook(bookId).getBody(); //it'll throw an exception if bookId doesn't exist in the Book Collection
		person.addBook(newBorrowedBook);
		personRepo.save(person);	//as it was previously checked that the person exists, it will update the existing document
		
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		header.setLocation(getUriForPersonBooks(personName, bookId));
		return new ResponseEntity<Void>(header, HttpStatus.CREATED);
	}
	
	
	
	
	
	
	// ------------------------------------- controller's private methods ------------------------------------------------
	
	
	private URI getUriForPersons(String personName) throws URISyntaxException{
		/*
		 * returns the URI for the 'Persons' collection if the parameter is empty, or the resource URI if a personName is specified.
		 */
		return new URI((ControllerLinkBuilder.linkTo(PersonController.class)).toString()+personName);
	}
	
	
	
	private URI getUriForPersonBooks(String personName, String bookId) 
							throws URISyntaxException, PersonDoesNotExistsException{
		/*
		 * returns the URI for the Person's collection of books, or for a particular book if bookId is specified.
		 */
		if (personName==null || personName.isEmpty()) throw new PersonDoesNotExistsException(personName);
		return new URI(getUriForPersons(personName).toString().concat("/books/"+bookId));
	}
	
	
	
	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	@ExceptionHandler({PersonDoesNotExistsException.class, PersonAlreadyExistsException.class})
	private String conflictHandler(Exception e){
		/*
		 * this is a different way to handle exceptions for the controller. From: http://www.infoq.com/articles/springmvc_jsx-rs
		 */
		return e.getMessage();
	}
}