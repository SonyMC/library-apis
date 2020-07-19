package com.sonymathew.course.apis.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class PublisherNotDeletedException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public PublisherNotDeletedException(String exceptionMessage) {
		super(exceptionMessage);

	}
	
	

}
