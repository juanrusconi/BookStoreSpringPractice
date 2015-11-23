package controller;

import org.springframework.data.mongodb.repository.MongoRepository;
import model.Book;

public interface BookRepository extends MongoRepository<Book, String>{
	
	public Book findByTitle(String title);
	
}
