package repository;

import exception.BookAlreadyExistsException;
import exception.BookDoesNotExistException;
import model.Book;

public interface BookRepository /* extends Repository<Book, Long> */{
	
	boolean create(Book newBook) throws BookAlreadyExistsException;
	
	Book get(String id) throws BookDoesNotExistException;
	
	String getAll();
	
	Book delete() throws BookDoesNotExistException;
	
	Book modify() throws BookDoesNotExistException;
}
