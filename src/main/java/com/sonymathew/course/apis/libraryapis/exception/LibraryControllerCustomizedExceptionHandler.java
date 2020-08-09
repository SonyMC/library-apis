package com.sonymathew.course.apis.libraryapis.exception;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sonymathew.course.apis.libraryapis.exception.model.LibraryApiError;
import com.sonymathew.course.apis.libraryapis.utils.LibraryApiUtils;



@ControllerAdvice
public class LibraryControllerCustomizedExceptionHandler extends ResponseEntityExceptionHandler {
	
	private static Logger logger = LoggerFactory.getLogger(LibraryControllerCustomizedExceptionHandler.class);
	
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		
		// Thsi is the generic excpetion handler mehod which is nto tied to any particular class. Hence we would need to generate a Trace ID.
		String traceId = getTraceId(request);
		LibraryApiError exceptionResponse =new LibraryApiError(traceId,new Date(), ex.getMessage(), request.getDescription(true));
		logger.error(traceId, ex);
		return new ResponseEntity<Object>(exceptionResponse,HttpStatus.INTERNAL_SERVER_ERROR);
	
	} 
	
	
	// utility method
	private String getTraceId(WebRequest request) {
		String traceId = request.getHeader("Trace-Id"); // geting trace if from header if it is available
		if(!LibraryApiUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}
		return traceId;
	}

	@ExceptionHandler(PublisherAlreadyExistsException.class)
	public final ResponseEntity<Object> publisherAlreadyExists(PublisherAlreadyExistsException ex, WebRequest request) {
		
		LibraryApiError exceptionResponse = new LibraryApiError(ex.getTraceId(),new Date(), ex.getMessage() , request.getDescription(true));
		logger.error(ex.getTraceId(), ex);
		return new ResponseEntity<Object>(exceptionResponse,HttpStatus.CONFLICT);
		
	}	
	
	@ExceptionHandler(PublisherNotFoundException.class)
	public final ResponseEntity<Object> publisherNotFound(PublisherNotFoundException ex, WebRequest request) throws Exception {
		
		LibraryApiError exceptionResponse = new LibraryApiError(ex.getTraceId(),new Date(), ex.getMessage() , request.getDescription(true));
		logger.error(ex.getTraceId(), ex);
		return new ResponseEntity<Object>(exceptionResponse,HttpStatus.NOT_FOUND);
	
		
	}	
	
	@ExceptionHandler(PublisherNotUpdatedException.class)
	public final ResponseEntity<Object> publisherNotUpdated(PublisherNotUpdatedException ex, WebRequest request) throws Exception {
		
		LibraryApiError exceptionResponse = new LibraryApiError(ex.getTraceId(),new Date(), ex.getMessage() , request.getDescription(true));
		logger.error(ex.getTraceId(), ex);
		return new ResponseEntity<Object>(exceptionResponse,HttpStatus.NOT_MODIFIED);
	
		
	}
	
	@ExceptionHandler(PublisherNotDeletedException.class)
	public final ResponseEntity<Object> publisherNotDeleted(PublisherNotDeletedException ex, WebRequest request) throws Exception {
		
		LibraryApiError exceptionResponse = new LibraryApiError(ex.getTraceId(),new Date(), ex.getMessage() , request.getDescription(true));
		logger.error(ex.getTraceId(), ex);
		return new ResponseEntity<Object>(exceptionResponse,HttpStatus.INTERNAL_SERVER_ERROR);
	
		
	}	
	
	@ExceptionHandler(PublisherBadRequestException.class)
	public final ResponseEntity<Object> publisherBadRequest(PublisherBadRequestException ex, WebRequest request) throws Exception {
		
		LibraryApiError exceptionResponse = new LibraryApiError(ex.getTraceId(),new Date(), ex.getMessage() , request.getDescription(true));
		logger.error(ex.getTraceId(), ex);
		return new ResponseEntity<Object>(exceptionResponse,HttpStatus.BAD_REQUEST);
	
		
	}	

}
