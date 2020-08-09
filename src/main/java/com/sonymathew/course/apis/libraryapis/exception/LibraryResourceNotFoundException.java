package com.sonymathew.course.apis.libraryapis.exception;

public class LibraryResourceNotFoundException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String traceId;

    public LibraryResourceNotFoundException(String traceId, String message) {
        super(message);
        this.traceId = traceId;
    }

    public String getTraceId() {
        return traceId;
    }
}
