package com.sonymathew.course.apis.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LibraryResourceAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public LibraryResourceAlreadyExistsException(String exceptionMessage) {
		super(exceptionMessage);

	}
	
	

}
