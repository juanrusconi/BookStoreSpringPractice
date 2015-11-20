package model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.hateoas.Link;


public class Book {
	
	@Id
	private String id;
	
	private String title;
	private String author;
	private String publisher;
	private List<Link> links = new ArrayList<Link>();;

	public Book(){		
	}

//	public Book(String title, String author, String publisher, List<Link> links) {
//		super();
//		this.title = title;
//		this.author = author;
//		this.publisher = publisher;
//		this.links = links;
//	}
	
	public Book(String id, String title, String author, String publisher, List<Link> links) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
		this.publisher = publisher;
		this.links = links;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	public void addLink(Link newLink){
		links.add(newLink);
	}

	@Override
	public String toString() {
		return "Book [id=" + id + ", title=" + title + ", author=" + author + ", publisher=" + publisher + "]";
	}
	
	
	
}
