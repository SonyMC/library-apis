package com.sonymathew.course.apis.libraryapis.exception;

public class LibraryResourceBadRequestException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String traceId;

    public LibraryResourceBadRequestException(String traceId, String message) {
        super(message);
        this.traceId = traceId;
    }

    public String getTraceId() {
        return traceId;
    }
}
