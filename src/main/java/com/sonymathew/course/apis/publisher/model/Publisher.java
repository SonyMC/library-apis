package com.sonymathew.course.apis.publisher.model;

public class Publisher {
	
	private String publisherID;
	private String name;
	private String emailId;
	private String phoneNumber;
	
	public Publisher() {

	}

	public Publisher(String publisherID, String name, String emailId, String phoneNumber) {
		super();
		this.publisherID = publisherID;
		this.name = name;
		this.emailId = emailId;
		this.phoneNumber = phoneNumber;
	}

	public String getPublisherID() {
		return publisherID;
	}

	public void setPublisherID(String publisherID) {
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
