package com.sonymathew.course.apis.library.exception.model;

import java.util.Date;

public class LibraryApiError {
	
	private String traceId;
	private Date timestamp;
	private String message;
	private String details;
	
	public LibraryApiError() {
		
	}

	public LibraryApiError(String traceId, Date timestamp, String message, String details) {
		super();
		this.traceId = traceId;
		this.timestamp = timestamp;
		this.message = message;
		this.details = details;
	}
	


	public String getTraceId() {
		return traceId;
	}

	public void setTraceID(String traceId) {
		this.traceId = traceId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return "LibraryAPIError [traceID=" + traceId + ", timestamp=" + timestamp + ", message=" + message
				+ ", details=" + details + "]";
	}
	
	
	



}
