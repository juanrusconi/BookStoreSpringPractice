package controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
/* ---- Spring HATEOAS ---- */
//import org.springframework.hateoas.Link;
//import org.springframework.hateoas.mvc.ControllerLinkBuilder;
/* ---- Spring HTTP ---- */
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
/* ---- Spring annotations ---- */
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import exception.BookAlreadyExistsException;
import exception.BookDoesNotExistException;
import model.Book;
import model.MyLink;


@RestController
@RequestMapping("/books")
public class BookController {
	
	@Autowired
	private BookRepository bookRepo;
	public static String books_uri = "http://localhost:8080/books/";
	private final AtomicLong counter = new AtomicLong();
	
	
	@RequestMapping(method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<Book>> getAllBooks(){
		return new ResponseEntity<List<Book>>(bookRepo.findAll(), HttpStatus.OK);
	}
	
	
	
	@RequestMapping(method=RequestMethod.GET, value = "/{bookId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Book> getBook(@PathVariable String bookId ) throws BookDoesNotExistException{
		return new ResponseEntity<Book>(bookRepo.findOne(bookId), HttpStatus.OK);
	}
	
	

	@RequestMapping(method=RequestMethod.POST)
	ResponseEntity<Void> createBook(@RequestParam(value="title", defaultValue="no_title") String title, 
									@RequestParam(value="author", defaultValue="no_author") String author, 
									@RequestParam(value="pub", defaultValue="no_publisher") String pub) throws BookAlreadyExistsException, URISyntaxException{
		Long newId = counter.incrementAndGet();
		while (bookRepo.exists(newId.toString())){
			newId = counter.incrementAndGet();
		}
		Book newBook = new Book(newId.toString(), title, author, pub, new ArrayList<MyLink>());
		
//		newBook.addLink(new Link(ControllerLinkBuilder.linkTo(BookController.class).slash(newBook.getId()).toString(), Link.REL_SELF));
//		newBook.addLink(new Link(ControllerLinkBuilder.linkTo(BookController.class).toString(), "coll"));
		newBook.addLink(new MyLink("self", books_uri+newBook.getId()));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
//		headers.setLocation(ControllerLinkBuilder.linkTo(BookController.class).slash(newBook.getId()).toUri());
		headers.setLocation(new URI(books_uri+newBook.getId()));
		HttpStatus operationStatus = (bookRepo.save(newBook)!=null)? HttpStatus.OK:HttpStatus.BAD_REQUEST;
		
		return new ResponseEntity<Void>(headers, operationStatus);
	}
	
	
	
	
	
	
	
	@ExceptionHandler({IllegalArgumentException.class, BookDoesNotExistException.class, BookAlreadyExistsException.class})
	ResponseEntity<String> conflictHandler(Exception e){
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.PRECONDITION_FAILED);
	}
	
}
