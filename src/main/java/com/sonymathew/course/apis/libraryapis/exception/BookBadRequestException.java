package com.sonymathew.course.apis.libraryapis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookBadRequestException extends Exception {
	private static final long serialVersionUID = 1L;
	private String traceId;;
	
	public BookBadRequestException(String traceId, String exceptionMessage) {
		super(exceptionMessage);
		this.traceId = traceId;

	}
	
	public String getTraceId() {
		return traceId;
	}
}
