package com.sonymathew.course.apis.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PublisherNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public PublisherNotFoundException(String exceptionMessage) {
		super(exceptionMessage);

	}
	
	

}
