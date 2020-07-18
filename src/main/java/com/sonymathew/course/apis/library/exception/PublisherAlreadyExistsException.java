package com.sonymathew.course.apis.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PublisherAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public PublisherAlreadyExistsException(String exceptionMessage) {
		super(exceptionMessage);

	}
	
	

}
