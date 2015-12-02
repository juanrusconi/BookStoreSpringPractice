package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Id;
/* ---- Spring HATEOAS ---- */
//import org.springframework.hateoas.Link;
//import org.springframework.hateoas.ResourceSupport;
/* ---- jackson ---- */
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Book{
	
	@Id
	private String id;
	
	private String title;
	private String author;
	private String publisher;
	private boolean hasCopiesLent;
	private Date addedOnDate;
	private Date lastModifiedOnDate;
	private List<MyLink> myLinks = new ArrayList<MyLink>();;

	@JsonCreator
	public Book(){		
	}

	@JsonCreator
	public Book(@JsonProperty("id") String id, 
				@JsonProperty("title") String title, 
				@JsonProperty("author") String author, 
				@JsonProperty("publisher") String publisher, 
				@JsonProperty("hasCopiesLent") boolean hasCopiesLent, 
				@JsonProperty("addedOnDate") Date addedOnDate,
				@JsonProperty("lastModifiedOnDate") Date lastModifiedOnDate, 
				@JsonProperty("links") List<MyLink> links) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
		this.publisher = publisher;
		this.hasCopiesLent = hasCopiesLent;
		this.addedOnDate = addedOnDate;
		this.lastModifiedOnDate = lastModifiedOnDate;
		this.myLinks = links;
	}

	public Date getLastModifiedOnDate() {
		return lastModifiedOnDate;
	}

	public void setLastModifiedOnDate(Date lastModifiedOnDate) {
		this.lastModifiedOnDate = lastModifiedOnDate;
	}

	public boolean getHasCopiesLent() {
		return hasCopiesLent;
	}

	public void setHasCopiesLent(boolean hasCopiesLended) {
		this.hasCopiesLent = hasCopiesLended;
	}

	public Date getAddedOnDate() {
		return addedOnDate;
	}

	public void setAddedOnDate(Date addedOnDate) {
		this.addedOnDate = addedOnDate;
	}

	public String getId() {
		return id;
	}

//	public String getBookId() {
//		return id;
//	}
	
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

	public List<MyLink> getLinks() {
		return myLinks;
	}

	public void setLinks(List<MyLink> links) {
		this.myLinks = links;
	}
	
	public void addLink(MyLink newLink){
		myLinks.add(newLink);
	}

	@Override
	public String toString() {
		return "Book [id=" + id + 
				", title=" + title + 
				", author=" + author + 
				", publisher=" + publisher	+
				", hasCopiesLent=" + hasCopiesLent + 
				", addedOnDate=" + addedOnDate + 
				", lastModifiedOnDate="	+ lastModifiedOnDate + 
				", links=" + myLinks + "]";
	}	
}
