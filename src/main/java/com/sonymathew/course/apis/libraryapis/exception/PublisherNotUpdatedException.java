package com.sonymathew.course.apis.libraryapis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_MODIFIED)
public class PublisherNotUpdatedException extends Exception {

	private static final long serialVersionUID = 1L;
	private String traceId;
	
	public PublisherNotUpdatedException(String traceId,String exceptionMessage) {
		super(exceptionMessage);
		this.traceId = traceId; 

	}

	public String getTraceId() {
		return traceId;
	}
	
	
	
	

}
