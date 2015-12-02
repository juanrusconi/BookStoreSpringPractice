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
//import org.springframework.hateoas.Link;
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
import model.MyLink;


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
	@ResponseBody 
	public List<Book> getAllBooks() throws CollectionIsEmptyException{
		/*
		 * Returns a List containing all the books stored in the database.
		 * Throws an exception if the database collection is empty.
		 */
		List<Book> collection = bookRepo.findAll();
		if (collection.isEmpty() || collection == null) throw new CollectionIsEmptyException("book");
		return collection;
	}
	
	
	
	@RequestMapping(method=RequestMethod.GET, value = BOOK_ID_URI, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody 
	public Book getBook(@PathVariable String bookId ) throws BookDoesNotExistException{
		/*
		 * Returns the object of type Book with the given Id from the database.
		 * An exception is thrown if no book can be found.
		 */
		if (bookRepo.exists(bookId)) 
			return bookRepo.findOne(bookId);
		throw new BookDoesNotExistException(bookId);
	}
	
	

	@RequestMapping(method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> createBook(	@RequestParam(value="title", defaultValue="no_title") String title, 
											@RequestParam(value="author", defaultValue="no_author") String author, 
											@RequestParam(value="pub", defaultValue="no_publisher") String pub) 
														throws BookAlreadyExistsException, URISyntaxException{
		/*
		 * Creates a new document in the books collection with the given parameters as fields.
		 * The Id of the new object is auto-generated.
		 * Returns an empty response in which header is contained the location (URI) of the newly created resource.
		 * An exception is thrown if there is an existing book with the same title.
		 */
		Long newId = counter.incrementAndGet();
		while (bookRepo.exists(newId.toString())){
			newId = counter.incrementAndGet();
		}
		Book newBook = new Book(newId.toString(), 
								title, author, pub, 
								false, 										//has copies lent? -> false by default
								calendar.getTime(), calendar.getTime(), 	//date of creation and date of last modification
								new ArrayList<MyLink>());
		newBook.addLink(new MyLink(getUriForBooks(newBook.getId()),MyLink.REL_SELF));
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setLocation(getUriForBooks(newBook.getId()));
		
		if (bookRepo.findByTitle(newBook.getTitle()) == null){
			HttpStatus operationStatus = (bookRepo.save(newBook)!=null)? HttpStatus.CREATED:HttpStatus.BAD_REQUEST;
			return new ResponseEntity<Void>(headers, operationStatus);
		}
		throw new BookAlreadyExistsException(newBook.getId());
	}
	
	
	
	@RequestMapping(method=RequestMethod.DELETE, value = BOOK_ID_URI, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteBook(@PathVariable String bookId ) 
			throws BookDoesNotExistException, BookHasCopiesLendedException, URISyntaxException{
		/*
		 * Deletes a book, previously checking if it has copies that have been borrowed by persons. If it does, 
		 * the method will throw an exception and the book wont be deleted. 
		 * Returns an empty response in which header is contained the location (URI) of the collection the resource was in.
		 */
		if (bookRepo.exists(bookId)){
			if (!bookRepo.findOne(bookId).getHasCopiesLent()){
				bookRepo.delete(bookId);			
				HttpHeaders headers = new HttpHeaders();
				headers.setLocation((getUriForBooks(null))); 
				
				return new ResponseEntity<Void>(headers, HttpStatus.OK);
			}
			throw new BookHasCopiesLendedException(bookId);
		}
		throw new BookDoesNotExistException(bookId);
	}
	
	
	
	@RequestMapping(method=RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
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
											@RequestParam(value="lended", defaultValue="true") boolean lended)
											throws BookDoesNotExistException, URISyntaxException {
		/*
		 * This method allows to update the fields 'author', 'publisher' and/or 'hasCopiesLended' of the book matching Id field.
		 * Automatically will update the 'lastModifiedOnDate' field with the current date time value.
		 * Returns an empty response in which header is contained the location (URI) of the updated resource.
		 * Will throw an exception if there isn't an existing book with the given Id.
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
		headers.setLocation((getUriForBooks(modifiedBook.getId())));
		
		return new ResponseEntity<Void>(headers, HttpStatus.OK);		
	}
	
	
	// ------------------------------------- controller's private methods ------------------------------------------------
	
	private URI getUriForBooks(String bookId) throws URISyntaxException{
		/*
		 * Returns the URI for the 'books' collection if the parameter is empty, 
		 * or a particular resource's URI if bookId is specified
		 */
		return new URI(ControllerLinkBuilder.linkTo(BookController.class) +  (bookId.isEmpty()? "" : "/"+bookId));
	}
	
	
	
	@ExceptionHandler({IllegalArgumentException.class, 
						BookAlreadyExistsException.class,
						BookHasCopiesLendedException.class})
	private ResponseEntity<String> conflictHandler_existingItem(Exception e){
		/*
		* This is a different way to handle exceptions for the controller. 
		* From: RESTFul API Design (SpringDeveloper) - https://www.youtube.com/watch?v=oG2rotiGr90
		*/
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.PRECONDITION_FAILED);
	}
	
	
	
	@ExceptionHandler({BookDoesNotExistException.class,
						CollectionIsEmptyException.class})
	private ResponseEntity<String> conflictHandler_missingItem(Exception e){
		/*
		* This is a different way to handle exceptions for the controller. 
		* From: RESTFul API Design (SpringDeveloper) - https://www.youtube.com/watch?v=oG2rotiGr90
		*/
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
	}
}