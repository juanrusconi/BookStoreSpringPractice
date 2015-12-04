package model;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

//@Document
public class Person {

	@Id
	String id;
	
	String name;
	List<Book> books = new ArrayList<Book>();	/* list of books borrowed by this person */
	List<MyLink> links = new ArrayList<MyLink>();
	
	@JsonIgnore
	String userpass;
    String userid;
	
	protected Person(){	
	}

	public Person(String name, 
				  List<Book> books, 
				  List<MyLink> links, 
				  String user_pass, 
				  String user_id) {
		super();
		this.name = name;
		this.books = books;
		this.links = links;
		this.userpass = user_pass;
		this.userid = user_id;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + 
				", name=" + name + 
				", books=" + books + 
				", links=" + links + 
				", userpass=" + userpass + 
				", userid=" + userid + "]";
	}

	public String getUserpass() {
		return userpass;
	}

	public void setUserpass(String user_pass) {
		this.userpass = user_pass;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String user_id) {
		this.userid = user_id;
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
	
	public boolean hasBook (String bookId){
		for (Book b:books){
			if (b.getId() == bookId || b.getId().equals(bookId)) return true;
		}	
		return false;
	}
	
	public boolean deleteBook (Book bookToRemove){
		return books.remove(bookToRemove);
	}
	
	public void deleteAllBooks (){
		books.clear();
	}
}
