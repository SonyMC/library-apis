package com.sonymathew.course.apis.libraryapis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookAlreadyExistsException extends Exception {
	private static final long serialVersionUID = 1L;
	private String traceId;;
	
	public BookAlreadyExistsException(String traceId, String exceptionMessage) {
		super(exceptionMessage);
		this.traceId = traceId;

	}
	
	public String getTraceId() {
		return traceId;
	}
}
