package controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


import org.springframework.data.mongodb.core.MongoTemplate;
/* ---- Spring HATEOAS ---- */
//import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
/* ---- Spring HTTP ---- */
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
import org.springframework.web.bind.annotation.RestController;
/* --- Exception classes --- */
import exception.BookAlreadyExistsException;
import exception.BookDoesNotExistException;
import exception.CollectionIsEmptyException;

import model.Book;
import model.MyLink;


@RestController
@RequestMapping("bookstore/books")
public class BookController {
	
	@Autowired
	public BookRepository bookRepo;
	public MongoTemplate mongoTemp;
	final AtomicLong counter = new AtomicLong();
	
	
	public BookController(){
		
	}
	
	
	
	@RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Book>> getAllBooks() throws CollectionIsEmptyException{
		List<Book> collection = bookRepo.findAll();
		if (!collection.isEmpty())
			return new ResponseEntity<List<Book>>(collection, HttpStatus.OK);			
		throw new CollectionIsEmptyException(mongoTemp.getCollectionName(Book.class));
	}
	
	
	
	@RequestMapping(method=RequestMethod.GET, value = "/{bookId}", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Book> getBook(@PathVariable String bookId ) throws BookDoesNotExistException{
		if (bookRepo.exists(bookId)) 
			return new ResponseEntity<Book>(bookRepo.findOne(bookId), HttpStatus.OK);
		throw new BookDoesNotExistException(bookId);
	}
	
	

	@RequestMapping(method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> createBook(@RequestParam(value="title", defaultValue="no_title") String title, 
									@RequestParam(value="author", defaultValue="no_author") String author, 
									@RequestParam(value="pub", defaultValue="no_publisher") String pub) 
														throws BookAlreadyExistsException, URISyntaxException{
		Long newId = counter.incrementAndGet();
		while (bookRepo.exists(newId.toString())){
			newId = counter.incrementAndGet();
		}
		Book newBook = new Book(newId.toString(), title, author, pub, new ArrayList<MyLink>());
		newBook.addLink(new MyLink(MyLink.REL_SELF, getUriForBooks(newBook.getId()).toString()));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setLocation((getUriForBooks(newBook.getId())));
		
		if (!bookRepo.exists(newBook.getId())){
			HttpStatus operationStatus = (bookRepo.save(newBook)!=null)? HttpStatus.CREATED:HttpStatus.BAD_REQUEST;
			return new ResponseEntity<Void>(headers, operationStatus);
		}
		throw new BookAlreadyExistsException(newBook.getId());
	}
	
	
	
	@RequestMapping(method=RequestMethod.DELETE, value = "/{bookId}", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteBook(@PathVariable String bookId ) throws BookDoesNotExistException, URISyntaxException{
		
		if (bookRepo.exists(bookId)){
			bookRepo.delete(bookId);			
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation((getUriForBooks(""))); //returns the URI of the collection it was in
			
			return new ResponseEntity<Void>(headers, HttpStatus.OK);
		}
		throw new BookDoesNotExistException(bookId);
	}
	
	
	// ------------------------------------- controller's private methods ------------------------------------------------
	
	URI getUriForBooks(String bookId) throws URISyntaxException{
		/*
		 * returns the URI for the 'books' collection if the parameter is empty, or the resource URI if a bookId is specified
		 */
		return new URI((ControllerLinkBuilder.linkTo(BookController.class)).toString()+bookId);
	}
	
	
	
	@ExceptionHandler({IllegalArgumentException.class, 
						BookAlreadyExistsException.class})
	ResponseEntity<String> conflictHandler_existingItem(Exception e){
		/*
		 * this is a different way to handle exceptions for the controller. From: https://www.youtube.com/watch?v=oG2rotiGr90
		 */
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.PRECONDITION_FAILED);
	}
	
	
	
	@ExceptionHandler({BookDoesNotExistException.class,
						CollectionIsEmptyException.class})
	ResponseEntity<String> conflictHandler_missingItem(Exception e){
		/*
		* this is a different way to handle exceptions for the controller. From: https://www.youtube.com/watch?v=oG2rotiGr90
		*/
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
	}
}