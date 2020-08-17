package com.sonymathew.course.apis.libraryapis.book;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import com.sonymathew.course.apis.libraryapis.author.AuthorEntity;
import com.sonymathew.course.apis.libraryapis.publisher.PublisherEntity;

@Entity
@Table(name = "BOOK")
public class BookEntity {
	
	@Column(name="Book_Id")
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "bookId_generator")
	@SequenceGenerator(name="bookId_generator", sequenceName="book_sequence", allocationSize=50)
	private int bookId;
	
	@Column(name="ISBN")
	private String isbn;
	
	@Column(name="Title")
	private String title;
		
	@Column(name="Year_Published")
	private Integer yearPublished;
	
	@Column(name="Edition")
	private String edition;	
	
	// Many books can have one publisher. Publisher Id is the fk to the table publisher. Mapping with book is many-to-one as the same publiher can publish many books
	@ManyToOne(fetch=FetchType.LAZY,cascade = CascadeType.ALL) //The meaning of CascadeType. ALL is that the persistence will propagate (cascade) all EntityManager operations ( PERSIST, REMOVE, REFRESH, MERGE, DETACH ) to the relating entities
	@JoinColumn(name="Publisher_Id", nullable= false)  // tells to which table column this is a foreign key of. Hibernate will automatically determine the table name.
	private PublisherEntity publisher;   
	
	
	@OneToOne(fetch=FetchType.LAZY, // Lazy is preferred else we might face exceptions bcos one entity may be loaded before teh other or might not be available
			  cascade = CascadeType.ALL,
			  mappedBy = "bookEntity") // note : here we do not mention join column name as relationship will be maintained by BookStatusEntity table. Refer commnets below.
	// book status entity has one-to-one relationship with book entity
	// mappedBy tells Hibernate that bookEntity is not responsible for the one-to-one relationship, but rather the responsibility is with book status entity.
	// So Hibernate should look for a field named bookEntity  in the BookStatusEntity to find the configuration for fk/joint column
	private BookStatusEntity bookStatus; 
	
	
	// many to may relationship between books and authors
	// The jointable annotation determines teh owner of the realtion in a fk/join table relationship
	@ManyToMany(fetch=FetchType.LAZY,
			   cascade = CascadeType.ALL)
	@JoinTable( name = "book_author", 
	            joinColumns = @JoinColumn(name="book_id"),  // book_id is teh foreign key to the book table in the book_author table
	            inverseJoinColumns = @JoinColumn(name="author_id"))    //author_id is fk for author table in the book_author table ; bi-directional association ; only one owner can be tehre of teh relationship ans here the authr entoty is thw owner 
	private Set<AuthorEntity> authors = new HashSet<>();// Set is the interface and hashset is the implementing class
	
	public BookEntity() {

	}

	public BookEntity(String isbn, String title, Integer yearPublished, String edition,
			PublisherEntity publisher, BookStatusEntity bookStatus, Set<AuthorEntity> authors) {
		this.isbn = isbn;
		this.title = title;
		this.yearPublished = yearPublished;
		this.edition = edition;
		this.publisher = publisher;
		this.bookStatus = bookStatus;
		this.authors = authors;
	}
	
	public BookEntity(String isbn, String title, Integer yearPublished, String edition) {
		this.isbn = isbn;
		this.title = title;
		this.yearPublished = yearPublished;
		this.edition = edition;
		
	}	
	

	public BookEntity(String isbn, String title, Integer yearPublished, String edition,PublisherEntity publisher) {
		this.isbn = isbn;
		this.title = title;
		this.yearPublished = yearPublished;
		this.edition = edition;
		this.publisher = publisher;
	}

	public int getBookId() {
		return bookId;
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

	public PublisherEntity getPublisher() {
		return publisher;
	}

	public void setPublisher(PublisherEntity publisher) {
		this.publisher = publisher;
	}

	public BookStatusEntity getBookStatus() {
		return bookStatus;
	}

	public void setBookStatus(BookStatusEntity bookStatus) {
		this.bookStatus = bookStatus;
	}

	public Set<AuthorEntity> getAuthors() {
		return authors;
	}

	public void setAuthors(Set<AuthorEntity> authors) {
		this.authors = authors;
	}

	
	
}
