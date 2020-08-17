package com.sonymathew.course.apis.libraryapis.publisher;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

// This is the model class which we will expose to the consumers. Thsi is linked to the entoty class which is hidden from the consumers.
// Note : Input Validation constraints will only work if we add dependency  'spring-boot-starter-validation' in POM
// For the validation work, we also have to use the @Valid annotation in the controller class methods where we use the Publisher object as teh Request body
@JsonInclude(JsonInclude.Include.NON_NULL)   // This annotation will remove hide any from the response 
public class Publisher {
	
	private Integer publisherID;
	
	
	@Size(min=3,max=50,message="Publisher name should be atleast 3 chars and not bigger than 50 chars" )
	private String name;
	
	@Email(message="Please enter a valid Email id")
	private String emailId;
	
	@Pattern(regexp = "\\d{3}-\\d{3}-\\d{3}", message ="Please enter phone number is format 123-456-789")
	private String phoneNumber;
	
	public Publisher() {

	}

	public Publisher(Integer publisherID, String name, String emailId, String phoneNumber) {
		super();
		this.publisherID = publisherID;
		this.name = name;
		this.emailId = emailId;
		this.phoneNumber = phoneNumber;
	}

	public Integer getPublisherID() {
		return publisherID;
	}

	public void setPublisherID(Integer publisherID) {
		this.publisherID = publisherID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return "Publisher [publisherID=" + publisherID + ", name=" + name + ", emailId=" + emailId + ", phoneNumber="
				+ phoneNumber + "]";
	}
	
	

}
