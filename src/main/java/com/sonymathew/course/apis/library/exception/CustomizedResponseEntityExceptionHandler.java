package com.sonymathew.course.apis.library.exception;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;



@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) throws Exception {
		
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(),ex.getMessage(),request.getDescription(true));
		return new ResponseEntity<Object>(exceptionResponse,HttpStatus.INTERNAL_SERVER_ERROR);
	
		
	}
	
	@ExceptionHandler(PublisherAlreadyExistsException.class)
	public final ResponseEntity<Object> publisherAlreadyExists(PublisherAlreadyExistsException ex, WebRequest request) throws Exception {
		
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(),ex.getMessage(),request.getDescription(true));
		return new ResponseEntity<Object>(exceptionResponse,HttpStatus.CONFLICT);
	
		
	}	
	
	@ExceptionHandler(PublisherNotFoundException.class)
	public final ResponseEntity<Object> publisherNotFound(PublisherNotFoundException ex, WebRequest request) throws Exception {
		
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(),ex.getMessage(),request.getDescription(true));
		return new ResponseEntity<Object>(exceptionResponse,HttpStatus.NOT_FOUND);
	
		
	}	

}
