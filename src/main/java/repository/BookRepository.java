package repository;

import exception.BookDoesNotExistException;
import model.Book;

public interface BookRepository {
	
	boolean create(Book newBook);
	/*Book*/String get(String id) throws BookDoesNotExistException;
	String getAll();
	Book delete() throws BookDoesNotExistException;
	Book modify() throws BookDoesNotExistException;
}
