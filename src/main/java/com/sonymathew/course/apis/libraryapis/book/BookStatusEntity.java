package com.sonymathew.course.apis.libraryapis.book;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="BOOK_STATUS")
public class BookStatusEntity {
	
	// This is a foreign key to the Book table and hence we will not be auto generating the id
	@Column(name="Book_Id")
	@Id
	private int bookId;
	
	// Book Status will be an enum
	@Column(name="State")
	@Enumerated(EnumType.STRING)
	private BookStatusState state;
	
	@Column(name = "Total_Number_Of_Copies")
	private int totalNumberOfCopies;


	@Column(name="Number_Of_Copies_Issued")
	private int numberOfCopiesIssued;

	// Since bookId is a fk to the book table with a one-to-one mapping, we will need to define the Book entity and the relation here 
	@OneToOne(fetch = FetchType.LAZY, optional = false) //The FetchType. LAZY tells Hibernate to only fetch the related entities from the database when you use the relationship
	//When you make the association mandatory (i.e. optional=false), Hiberante trusts you and assumes that a Book_id exists, since the association is mandatory
	@JoinColumn(name="Book_Id", nullable= false)  // tells to which table column this is a foreign key of. Hibernate will automatically determine the table name.
	private BookEntity bookEntity;
	
	
	// Constructor based dependency injection 	
	
	public BookStatusEntity() {
	}
	
	
	

	public BookStatusEntity(int bookId) {
		this.bookId = bookId;
	}

	

	public BookStatusEntity(int bookId, BookStatusState state, int totalNumbeOfCopies, int numberOfCopiesIssued) {
		this.bookId = bookId;
		this.state = state;
		this.totalNumberOfCopies = totalNumbeOfCopies;
		this.numberOfCopiesIssued = numberOfCopiesIssued;
	}




	public int getBookId() {
		return bookId;
	}




	public void setBookId(int bookId) {
		this.bookId = bookId;
	}




	public BookStatusState getState() {
		return state;
	}




	public void setState(BookStatusState state) {
		this.state = state;
	}




	public int getNumberOfCopiesIssued() {
		return numberOfCopiesIssued;
	}




	public void setNumberOfCopiesIssued(int numberOfCopiesIssued) {
		this.numberOfCopiesIssued = numberOfCopiesIssued;
	}




	public BookEntity getBookEntity() {
		return bookEntity;
	}




	public void setBookEntity(BookEntity bookEntity) {
		this.bookEntity = bookEntity;
	}

	public int getTotalNumberOfCopies() {
		return totalNumberOfCopies;
	}



	public void setTotalNumberOfCopies(int totalNumberOfCopies) {
		this.totalNumberOfCopies = totalNumberOfCopies;
	}


	

}
