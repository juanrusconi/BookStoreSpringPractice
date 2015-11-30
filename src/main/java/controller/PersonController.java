package controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/* ---- Spring annotations ---- */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
	PersonRepository personRepo;						//TODO: set to private
	@Autowired
	BookController bookCont = new BookController();		
	MongoTemplate mongoTemp;
	private Calendar calendar = Calendar.getInstance();
	static final String PERSON_ID_URI = "/{personName}";
	
	
	@RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Person>> getAllPersons() throws CollectionIsEmptyException{
		List<Person> collection = personRepo.findAll();
		if (collection.isEmpty() || collection == null) throw new CollectionIsEmptyException("person");
		return new ResponseEntity<List<Person>>(collection, HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value=PERSON_ID_URI, method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Person> getPerson(@PathVariable("personName") String name) throws PersonDoesNotExistsException{
		if (personRepo.findByName(name)!=null) 
			return new ResponseEntity<Person>(personRepo.findByName(name), HttpStatus.OK);
		throw new PersonDoesNotExistsException(name);
	}
	
	
	
	@RequestMapping(method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> createPerson (@RequestParam(value="name", required=true) String name) 
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
	
	
	
	@RequestMapping(method=RequestMethod.DELETE, value = PERSON_ID_URI, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deletePerson(@PathVariable String personName ) throws PersonDoesNotExistsException, URISyntaxException{
		
		if (personRepo.findByName(personName)!=null){
			personRepo.deleteByName(personName);			
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation((getUriForPersons(""))); //returns the URI of the collection it was in
			
			return new ResponseEntity<Void>(headers, HttpStatus.OK);
		}
		throw new PersonDoesNotExistsException(personName);
	}
	
	
	
	@RequestMapping(method=RequestMethod.DELETE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteAllPerson() throws URISyntaxException{
		/*
		 * deletes all the documents in the persons collection		
		 */
		personRepo.deleteAll();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation((getUriForPersons("")));
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}
	
	
	// ------------------------------------- person's books (reservations) methods ------------------------------------------------	
	
	static final String PERSONS_BOOKS_URI = PERSON_ID_URI + "/books";
	static final String BOOK_ID_URI = "/{bookId}";
	
	@RequestMapping(method=RequestMethod.GET, value = PERSONS_BOOKS_URI, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Book>> getAllPersonBooks(@PathVariable String personName) throws PersonDoesNotExistsException{
		/*
		 * returns the list of books from the given person
		 */
		return new ResponseEntity<List<Book>>(getPerson(personName).getBody().getBooks(), HttpStatus.OK);
	}
	
	
	
	@RequestMapping(method=RequestMethod.GET, value = PERSONS_BOOKS_URI + BOOK_ID_URI, produces=MediaType.APPLICATION_JSON_VALUE)
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
	
	
	
	@RequestMapping(method=RequestMethod.POST, value = PERSONS_BOOKS_URI + BOOK_ID_URI, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> addPersonsBook (@PathVariable String personName, @PathVariable String bookId)
			throws PersonDoesNotExistsException, BookAlreadyExistsException, BookDoesNotExistException, URISyntaxException{
		/*
		 * adds the given book to the specified person's book list, previously checking that the book exists.
		 * Returns the location of the newly created resource.
		 */
		Person person = getPerson(personName).getBody();
		if (person.findBook(bookId) != null) throw new BookAlreadyExistsException(bookId);
		Book newBorrowedBook = bookCont.getBook(bookId).getBody(); //it'll throw an exception if bookId doesn't exist in the Book Collection
		
		if (!newBorrowedBook.getHasCopiesLent()){	//the book is updated to indicate that people possess copies of it
			newBorrowedBook.setHasCopiesLent(true);
			newBorrowedBook.setLastModifiedOnDate(calendar.getTime());
			bookCont.updateBook(bookId, "", "", true);
		}
		
		person.addBook(newBorrowedBook);
		personRepo.save(person);	//as it was previously checked that the person exists, it will update the existing document
		
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		header.setLocation(getUriForPersonBooks(personName, bookId));
		return new ResponseEntity<Void>(header, HttpStatus.CREATED);
	}
	
	
	
	@RequestMapping(method=RequestMethod.DELETE, value = PERSONS_BOOKS_URI + BOOK_ID_URI, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deletePersonBook(@PathVariable String personName, @PathVariable String bookId) 
					throws PersonDoesNotExistsException, BookAlreadyExistsException, BookDoesNotExistException, URISyntaxException{
		/*
		 * deletes the given book from the person's book list and saves the changes.
		 */
		Person person = getPerson(personName).getBody();
		Book bookToRemove = person.findBook(bookId);
		if (bookToRemove == null) throw new BookDoesNotExistException(bookId);
		
		
		//TODO: set HasCopiesLent to false if no person has this book (call new method?)
		
		
		HttpStatus operation_status;
		HttpHeaders headers = new HttpHeaders();
		if (person.deleteBook(bookToRemove)){
			personRepo.save(person);
			operation_status = HttpStatus.OK;
			headers.setLocation((getUriForPersonBooks(personName, ""))); //returns the URI of the person's book list
		}
		else operation_status = HttpStatus.BAD_REQUEST;
		
		return new ResponseEntity<Void>(headers, operation_status);
	}
	
	
	
	@RequestMapping(method=RequestMethod.DELETE, value = PERSONS_BOOKS_URI, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteAllPersonBook(@PathVariable String personName) 
					throws PersonDoesNotExistsException, URISyntaxException{
		/*
		 * deletes the entire book list of the given person.
		 */
		Person person = getPerson(personName).getBody();
		person.deleteAllBooks();
		personRepo.save(person);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation((getUriForPersons(personName))); //returns the URI of the person
		
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}
	
	
	
	// ------------------------------------- controller's private methods ------------------------------------------------
	
	private URI getUriForPersons(String personName) throws URISyntaxException{
		/*
		 * returns the URI for the 'Persons' collection if the parameter is empty, or the resource URI if a personName is specified.
		 */
		return new URI((ControllerLinkBuilder.linkTo(PersonController.class)).toString()+"/"+personName);	//TODO: .slash(person)
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
	@ExceptionHandler({IllegalArgumentException.class, 
						PersonAlreadyExistsException.class,
						BookAlreadyExistsException.class})
	private String conflictHandler_existingItem(Exception e){
		/*
		 * this is a different way to handle exceptions for the controller. From: http://www.infoq.com/articles/springmvc_jsx-rs
		 */
		return e.getMessage();
	}
	
	
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler({PersonDoesNotExistsException.class,
						BookDoesNotExistException.class,
						CollectionIsEmptyException.class})
	private String conflictHandler_missingItem(Exception e){
		/*
		 * this is a different way to handle exceptions for the controller. From: http://www.infoq.com/articles/springmvc_jsx-rs
		 */
		return e.getMessage();
	}
}