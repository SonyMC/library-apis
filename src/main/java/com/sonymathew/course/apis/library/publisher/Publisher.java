package com.sonymathew.course.apis.library.publisher;

public class Publisher {
	
	private Integer publisherID;
	private String name;
	private String emailId;
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
