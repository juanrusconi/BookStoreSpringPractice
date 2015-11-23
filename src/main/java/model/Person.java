package model;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;

public class Person {

	@Id
	private String id;
	
	private String fname;
	private String lname;
	private List<Book> books = new ArrayList<Book>();	/* list of books borrowed by this person */
	private List<MyLink> links = new ArrayList<MyLink>();
	
	protected Person(){
		
	}

	public Person(String id, String fname, String lname, List<Book> books, List<MyLink> links) {
		super();
		this.id = id;
		this.fname = fname;
		this.lname = lname;
		this.books = books;
		this.links = links;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", fname=" + fname + ", lname=" + lname + ", books=" + books + ", links=" + links
				+ "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
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
	
	
	
}
