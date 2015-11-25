package model;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;

public class Person {

	@Id
	String id;
	
	String name;
	List<Book> books = new ArrayList<Book>();	/* list of books borrowed by this person */
	List<MyLink> links = new ArrayList<MyLink>();
	
	protected Person(){	
	}

	public Person(String name, ArrayList<Book> books, ArrayList<MyLink> links) {
		super();
		this.name = name;
		this.books = books;
		this.links = links;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", name=" + name + ", books=" + books + ", links=" + links + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}

	public List<MyLink> getLinks() {
		return links;
	}

	public void setLinks(List<MyLink> links) {
		this.links = links;
	}
	
	public void addLink(MyLink newLink){
		links.add(newLink);
	}
	
	public void addBook(Book newBook){
		books.add(newBook);
	}
	
	public Book findBook (String bookId){
		for (Book b:books){
			if (b.getId() == bookId || b.getId().equals(bookId)) return b;
		}	
		return null;
	}
	
	//TODO: methods for managing collection of borrowed books 
	
}
