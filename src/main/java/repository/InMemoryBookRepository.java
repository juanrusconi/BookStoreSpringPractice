package repository;

import com.mongodb.util.JSON;

import exception.BookAlreadyExistsException;
import exception.BookDoesNotExistException;
import model.Book;

public class InMemoryBookRepository implements BookRepository {
	
	MongoService mongoService = new MongoService();
	
	
	@Override
	public boolean create(Book newBook) throws BookAlreadyExistsException{
		/*
		 * first checks if the book exists in the database, 
		 * 	then if it doesn't it will create a new document for it.
		 * Returns true if the creation was successful.
		 */		
		
		//TODO: throw new BookAlreadyExistsException(newBook.getId())
		
		return (!mongoService.existingDoc(newBook.getId(), Book.class))? mongoService.createDoc(newBook):false;
	}


	
	@Override
	public Book get(String id) throws BookDoesNotExistException {
		return mongoService.getDoc(id, Book.class);
	}

	
	
	@Override
	public Book delete() throws BookDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}
	
	

	@Override
	public Book modify() throws BookDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	@Override
	public String getAll() {
		return mongoService.getColl(Book.class);
	}

	
}
