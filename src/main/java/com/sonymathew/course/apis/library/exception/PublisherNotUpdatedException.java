package com.sonymathew.course.apis.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_MODIFIED)
public class PublisherNotUpdatedException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public PublisherNotUpdatedException(String exceptionMessage) {
		super(exceptionMessage);

	}
	
	

}
