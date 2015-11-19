package controller;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import exception.BookDoesNotExistException;
import model.Book;
import repository.InMemoryBookRepository;

@RestController
@RequestMapping("/books")
public class BookController {
	
	private InMemoryBookRepository bookRepo = new InMemoryBookRepository();
	private final AtomicLong counter = new AtomicLong();	
	
	@RequestMapping(method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> getAllBooks(){
		return new ResponseEntity<String>(bookRepo.getAll(), HttpStatus.OK);
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value = "/{bookId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String/*Book*/> getBook(@PathVariable String bookId ) throws BookDoesNotExistException{
		return new ResponseEntity<String/*Book*/>(bookRepo.get(bookId), HttpStatus.OK);
	}
	
	
	
	@RequestMapping(method=RequestMethod.POST, value = "/{bookTitle}&{bookAuthor}&{bookPub}")
	ResponseEntity<Void> createBook(@PathVariable String bookTitle, @PathVariable String bookAuthor, @PathVariable String bookPub){
		
			
		Book newBook = new Book(counter.incrementAndGet(), bookTitle, bookAuthor, bookPub, new ArrayList<Link>());
		
		newBook.addLink(new Link(ControllerLinkBuilder.linkTo(BookController.class).slash(newBook.getId()).toString(), Link.REL_SELF));
//		newBook.addLink(new Link(ControllerLinkBuilder.linkTo(BookController.class).toString(), "collection"));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setLocation(ControllerLinkBuilder.linkTo(BookController.class).slash(newBook.getId()).toUri());
		HttpStatus operationStatus = (bookRepo.create(newBook))? HttpStatus.CREATED:HttpStatus.BAD_REQUEST;
		
		System.out.println("new object created with the following parameters: " + newBook.toString());
		
		return new ResponseEntity<Void>(headers, operationStatus);
	}
	
	
	
	
	
	
	
	@ExceptionHandler({IllegalArgumentException.class, BookDoesNotExistException.class})
	ResponseEntity<String> conflictHandler(Exception e){
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
	}
	
}
