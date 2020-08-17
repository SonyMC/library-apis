package com.sonymathew.course.apis.libraryapis.book;


import javax.validation.constraints.Pattern;

public class BookStatus {
	
	private Integer bookId;  // Using Integer instead of int as this is a wrapper class while int is a primitive. A wrapper class gives more functionlaties.

	private BookStatusState state;
	
	@Pattern(regexp = "[1-9][0-9]") // can be any two digit number between 1 and 99.
	private int totalNumberOfCopies;

	@Pattern(regexp = "[1-9][0-9]") // can be any two digit number between 1 and 99.
	private int numberOfCopiesIssued;


	
	// Constructor based dependency injection
	
	public BookStatus() {

	}

	public BookStatus(BookStatusState state, int totalNumberOfCopies,int numberOfCopiesIssued) {
		this.state = state;
		this.totalNumberOfCopies = totalNumberOfCopies;
		this.numberOfCopiesIssued = numberOfCopiesIssued;
	}
	public BookStatus(int bookId,BookStatusState state, int totalNumberOfCopies,int numberOfCopiesIssued) {
		this.bookId = bookId;
		this.state = state;
		this.totalNumberOfCopies = totalNumberOfCopies;
		this.numberOfCopiesIssued = numberOfCopiesIssued;
	}


	public Integer getBookId() {
		return bookId;
	}


	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}


	public BookStatusState getState() {
		return state;
	}


	public void setState(BookStatusState state) {
		this.state = state;
	}


	public int getTotalNumberOfCopies() {
		return totalNumberOfCopies;
	}


	public void setTotalNumberOfCopies(int totalNumberOfCopies) {
		this.totalNumberOfCopies = totalNumberOfCopies;
	}


	public int getNumberOfCopiesIssued() {
		return numberOfCopiesIssued;
	}


	public void setNumberOfCopiesIssued(int numberOfCopiesIssued) {
		this.numberOfCopiesIssued = numberOfCopiesIssued;
	}



}
