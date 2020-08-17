package com.sonymathew.course.apis.libraryapis.book;

import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sonymathew.course.apis.libraryapis.author.Author;

// This is the model class which we will expose to the consumers. Thsi is linked to the entoty class which is hidden from the consumers.
// Note : Input Validation constraints will only work if we add dependency  'spring-boot-starter-validation' in POM
// For the validation work, we also have to use the @Valid annotation in the controller class methods where we use the Book object as teh Request body

@JsonInclude(JsonInclude.Include.NON_NULL)   // This annotation will remove hide any from the response 
public class Book {
	
	private Integer bookId;
	
	
	@NotNull
	@Size(min=3,max=50,message="ISBN should be atleast 3 chars and not bigger than 50 chars" )
	private String isbn;
	
	@NotNull
	@Size(min=3,max=50,message="Book title should be atleast 3 chars and not bigger than 50 chars" )
	private String title;
	
	private Integer publisherId;
	
	private Integer yearPublished;
	
	@Size(min=3,max=20,message="Book edition should be atleast 3 chars and not bigger than 20 chars" )
	private String edition;	
	
	private BookStatus bookStatus;	
	
	public Set<Author> authors = new HashSet<>();  // Set is the Interface and HashSet is the Implementing class 
	
	public Book() {

	}

	public Book(Integer bookId,
			    String isbn,
			    String title,
			    Integer publisherId, 
			    Integer yearPublished,
			    String edition,
			    BookStatus bookStatus, 
			    Set<Author> authors) {

		this.bookId = bookId;
		this.isbn = isbn;
		this.title = title;
		this.publisherId = publisherId;
		this.yearPublished = yearPublished;
		this.edition = edition;
		this.bookStatus = bookStatus;
		this.authors = authors;
	}
	
	public Book(Integer bookId,
		    String isbn,
		    String title,
		    Integer publisherId, 
		    Integer yearPublished,
		    String edition,
		    BookStatus bookStatus){ 
	this.bookId = bookId;
	this.isbn = isbn;
	this.title = title;
	this.publisherId = publisherId;
	this.yearPublished = yearPublished;
	this.edition = edition;
	this.bookStatus = bookStatus;

}

	public Book(String isbn,
		    String title,
		    Integer publisherId, 
		    Integer yearPublished,
		    String edition,
		    BookStatus bookStatus) {
	this.isbn = isbn;
	this.title = title;
	this.publisherId = publisherId;
	this.yearPublished = yearPublished;
	this.edition = edition;
	this.bookStatus = bookStatus;
	}	


	public Integer getBookId() {
		return bookId;
	}

	public void setBookID(Integer bookId) {
		this.bookId = bookId;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Integer publisherId) {
		this.publisherId = publisherId;
	}

	public Integer getYearPublished() {
		return yearPublished;
	}

	public void setYearPublished(Integer yearPublished) {
		this.yearPublished = yearPublished;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public BookStatus getBookStatus() {
		return bookStatus;
	}

	public void setBookStatus(BookStatus bookStatus) {
		this.bookStatus = bookStatus;
	}

	public Set<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(Set<Author> authors) {
		this.authors = authors;
	}

	

}
