package controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/* ---- Spring annotations ---- */
import org.springframework.beans.factory.annotation.Autowired;
/* ---- Spring HATEOAS ---- */
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
/* ---- Spring HTTP ---- */
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/* --- Exception classes --- */
import exception.BookAlreadyExistsException;
import exception.BookDoesNotExistException;
import exception.BookHasCopiesLendedException;
import exception.CollectionIsEmptyException;
import model.Book;
//import model.MyLink;


@RestController
@RequestMapping("bookstore/books")
public class BookController {
	
	@Autowired
	BookRepository bookRepo;
	Calendar calendar = Calendar.getInstance();
	final AtomicLong counter = new AtomicLong();
	public static final String BOOK_ID_URI = "/{bookId}";
	
	
	public BookController(){
		
	}
	
	
	
	@RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Book>> getAllBooks() throws CollectionIsEmptyException{
		List<Book> collection = bookRepo.findAll();
		if (collection.isEmpty() || collection == null) throw new CollectionIsEmptyException("book");
		return new ResponseEntity<List<Book>>(collection, HttpStatus.OK);
	}
	
	
	
	@RequestMapping(method=RequestMethod.GET, value = BOOK_ID_URI, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public Book getBook(@PathVariable String bookId ) throws BookDoesNotExistException{
		if (bookRepo.exists(bookId)) 
			return bookRepo.findOne(bookId);
		throw new BookDoesNotExistException(bookId);
	}
	
	

	@RequestMapping(method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> createBook(	@RequestParam(value="title", defaultValue="no_title") String title, 
											@RequestParam(value="author", defaultValue="no_author") String author, 
											@RequestParam(value="pub", defaultValue="no_publisher") String pub) 
														throws BookAlreadyExistsException, URISyntaxException{
		/**
		 * creates a new document in the books collection with the given parameters as fields
		 */
		Long newId = counter.incrementAndGet();
		while (bookRepo.exists(newId.toString())){
			newId = counter.incrementAndGet();
		}
		Book newBook = new Book(newId.toString(), title, author, pub, false, calendar.getTime(), calendar.getTime(), new ArrayList<Link>());
		newBook.addLink(new Link(getUriForBooks(newBook).toString(),Link.REL_SELF));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setLocation((getUriForBooks(newBook)));
		
		if (!bookRepo.exists(newBook.getId())){
			HttpStatus operationStatus = (bookRepo.save(newBook)!=null)? HttpStatus.CREATED:HttpStatus.BAD_REQUEST;
			return new ResponseEntity<Void>(headers, operationStatus);
		}
		throw new BookAlreadyExistsException(newBook.getId());
	}
	
	
	
	@RequestMapping(method=RequestMethod.DELETE, value = BOOK_ID_URI, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteBook(@PathVariable String bookId ) 
			throws BookDoesNotExistException, BookHasCopiesLendedException, URISyntaxException{
		/*
		 * deletes a book, previously checking if it has copies that have been borrowed by persons. If it does, 
		 * the method will throw an exception and the book wont be deleted. 
		 */
		if (bookRepo.exists(bookId)){
			if (!bookRepo.findOne(bookId).getHasCopiesLent()){
				bookRepo.delete(bookId);			
				HttpHeaders headers = new HttpHeaders();
				headers.setLocation((getUriForBooks(null))); //returns the URI of the collection it was in
				
				return new ResponseEntity<Void>(headers, HttpStatus.OK);
			}
			throw new BookHasCopiesLendedException(bookId);
		}
		throw new BookDoesNotExistException(bookId);
	}
	
	
	
	@RequestMapping(method=RequestMethod.DELETE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteAllBooks() throws URISyntaxException{
		/*
		 * deletes all the documents in the book collection, WITHOUT checking if they have copies lent
		 */
		bookRepo.deleteAll();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation((getUriForBooks(null)));
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}
	
	
	
	@RequestMapping(method=RequestMethod.PUT, value = BOOK_ID_URI, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> updateBook(	@PathVariable String bookId,
											@RequestParam(value="author") String author, 
											@RequestParam(value="pub") String pub,
											@RequestParam(value="lended", defaultValue="false") boolean lended)
											throws BookDoesNotExistException, URISyntaxException {
		/*
		 * modifies the 'author' and/or 'publisher' fields of the given book, and returns the URI of the updated document.
		 */		
		if (!bookRepo.exists(bookId)) throw new BookDoesNotExistException(bookId);
		Book modifiedBook = bookRepo.findOne(bookId);
		
		if (!author.isEmpty()) modifiedBook.setAuthor(author);
		if (!pub.isEmpty()) modifiedBook.setPublisher(pub);
		modifiedBook.setHasCopiesLent(lended);
		modifiedBook.setLastModifiedOnDate(calendar.getTime());
		bookRepo.save(modifiedBook);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setLocation((getUriForBooks(modifiedBook)));
		
		return new ResponseEntity<Void>(headers, HttpStatus.OK);		
	}
	
	
	// ------------------------------------- controller's private methods ------------------------------------------------
	
	private URI getUriForBooks(Book book) throws URISyntaxException{
		/*
		 * returns the URI for the 'books' collection if the parameter is empty, or a particular resource's URI if a bookId is specified
		 */
		
		//TODO: {bookId} is not being set at the end of the returned URI
		
		//TODO: not required fields are being added automatically for each link object
		
		URI newUri = new URI(ControllerLinkBuilder.linkTo(BookController.class).slash(book).toString());
		return newUri;
	}
	
	
	
	@ExceptionHandler({IllegalArgumentException.class, 
						BookAlreadyExistsException.class,
						BookHasCopiesLendedException.class})
	private ResponseEntity<String> conflictHandler_existingItem(Exception e){
		/*
		 * this is a different way to handle exceptions for the controller. From: https://www.youtube.com/watch?v=oG2rotiGr90
		 */
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.PRECONDITION_FAILED);
	}
	
	
	
	@ExceptionHandler({BookDoesNotExistException.class,
						CollectionIsEmptyException.class})
	private ResponseEntity<String> conflictHandler_missingItem(Exception e){
		/*
		* this is a different way to handle exceptions for the controller. From: https://www.youtube.com/watch?v=oG2rotiGr90
		*/
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
	}
}