package com.sonymathew.course.apis.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class PublisherNotDeletedException extends Exception {

	private static final long serialVersionUID = 1L;
	private String traceId;
	
	public PublisherNotDeletedException(String traceId, String exceptionMessage) {
		super(exceptionMessage);
		this.traceId = traceId;

	}
	
	public String getTraceId() {
		return traceId;
	}
	

}
