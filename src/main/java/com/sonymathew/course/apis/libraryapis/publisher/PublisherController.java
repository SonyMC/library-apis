package com.sonymathew.course.apis.libraryapis.publisher;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sonymathew.course.apis.libraryapis.exception.PublisherAlreadyExistsException;
import com.sonymathew.course.apis.libraryapis.exception.PublisherBadRequestException;
import com.sonymathew.course.apis.libraryapis.exception.PublisherNotDeletedException;
import com.sonymathew.course.apis.libraryapis.exception.PublisherNotFoundException;
import com.sonymathew.course.apis.libraryapis.exception.PublisherNotUpdatedException;
import com.sonymathew.course.apis.libraryapis.utils.LibraryApiUtils;


@RestController
@RequestMapping(path="/v1/publishers")
public class PublisherController {
	
@Autowired	
private PublisherService publisherService;


private static Logger logger = LoggerFactory.getLogger(PublisherController.class);

public PublisherController(PublisherService publisherService) {
	this.publisherService = publisherService;
}


//	@GetMapping(path="/v1/publishers")
//	public String HelloWorld(){
//		return ("Welcome To World of Awesome Printing By Prentice!!!");
//	}
//	


	// Get publisher by id 
	@GetMapping(path = "/{publisherId}")
	public ResponseEntity<?> getPublisherbyId(@PathVariable Integer publisherId,
			  								  @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId) throws PublisherNotFoundException{
		
		
		
		
		// Now check if trace id is provided in request by consumer. IF not, we will generate it 
		if(!LibraryApiUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}
		
		logger.info("Trace ID : {}, Request to retrieve Publisher By ID: {}", traceId, publisherId);
		
		// Note : The exception handling has been moved out to the service class 
		Publisher publisherbyId = publisherService.getPublisherbyId(publisherId, traceId);
		return new ResponseEntity<>(publisherbyId,HttpStatus.FOUND);
		

	}
	
	
	// Get publisher by name
	@GetMapping(path = "/name/{publisherName}")
	public ResponseEntity<?> getPublisherByName(@PathVariable String publisherName,
												@RequestHeader(value = "Trace-Id", defaultValue = "") String traceId) throws PublisherNotFoundException{
		
		
		// Now check if trace id is provided in request by consumer. IF not, we will generate it 
		if(!LibraryApiUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}	
		
		logger.info("Trace ID : {}, Request to retrieve Publisher By Name : {}", traceId, publisherName);
		
		// Note : The exception handling has been moved out to the service class 
		List<Publisher> publisherList = publisherService.getPublisherbyName(publisherName, traceId);
		return new ResponseEntity<>(publisherList,HttpStatus.FOUND);
		

	}
		
	// Search publisher by name provided in uri query; Note that instead of @PathVariable in parm we use @RequestParam
	@GetMapping(path = "/search")
	public ResponseEntity<?> searchPublisherByName(@RequestParam String publisherName,
												   @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId) throws PublisherNotFoundException, PublisherBadRequestException{
		
		

		//Check that name query is provided
		if(!LibraryApiUtils.doesStringValueExist(publisherName)){
			
			// the commented our Response entity exception message is now replaced by teh curomized exception handler class/method 
			//return new ResponseEntity<>("Please provide name to be searched as a query in the uri  ", HttpStatus.BAD_REQUEST);
			throw new PublisherBadRequestException(traceId, "Please provide name to be searched as a query in the uri!!!");
		}
		
		logger.info("Trace ID : {}, Request to retrieve Publisher By Name as a query in uri : {}", traceId, publisherName);
		
		// Now check if trace id is provided in request by consumer. IF not, we will generate it 
		if(!LibraryApiUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}		
		
		// Note : The exception handling has been moved out to the service class 
		List<Publisher> publisherList = publisherService.searchPublisher(publisherName, traceId);
		return new ResponseEntity<>(publisherList,HttpStatus.FOUND);
		

	}	
	
	// Create a new publisher 
	// Note : Wherever we use Publisher model we have to use @Valid annotation for the validation sin that class to take affect 
	@PostMapping
	public ResponseEntity<?> addPublisher(@Valid @RequestBody Publisher publisher,
										  @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId) throws PublisherAlreadyExistsException{

		
		
		// Now check if trace id is provided in request by consumer. IF not, we will generate it 
		if(!LibraryApiUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}	
		
		logger.info("Trace ID : {}, Request to create Publisher: {}", traceId,publisher);
		
		// Note : The exception handling has been moved out to the service class 
		publisherService.addPublisher(publisher, traceId);
		return new ResponseEntity<>(publisher, HttpStatus.CREATED);
	}
	
	
	//Update a Publisher
	@PutMapping(path = "/{publisherId}")
	public ResponseEntity<?> UpdatePublisherbyId(@PathVariable Integer publisherId,
												 @ Valid @RequestBody Publisher publisherTobeUpdated,
												 @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId) throws PublisherNotFoundException, PublisherNotUpdatedException{
		
		
		// Now check if trace id is provided in request by consumer. IF not, we will generate it 
		if(!LibraryApiUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}
		
		logger.info("Trace ID : {}, Request to update Publisher: {}", traceId,publisherTobeUpdated);
		
		// We need to set the publisher id with what has been supplied in the path ,else whatever publisher is passed in parm will be updated.
		publisherTobeUpdated.setPublisherID(publisherId);
		// Note : The exception handling has been moved out to the service class 
		publisherService.updatePublisherbyId(publisherTobeUpdated, traceId);
		return new ResponseEntity<>(publisherTobeUpdated,HttpStatus.OK);
		

	}
	
	// Delete publisher by id 
	@DeleteMapping(path = "/{publisherId}")
	public ResponseEntity<?> deletePublisherbyId(@PathVariable Integer publisherId,
			 									 @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId) throws PublisherNotFoundException, PublisherNotDeletedException{
		
		// Now check if trace id is provided in request by consumer. IF not, we will generate it 
		if(!LibraryApiUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}
		
		
		logger.info("Trace ID : {}, Request to delete Publisher: {}", traceId,publisherId);
		// Note : The exception handling has been moved out to the service class 
		publisherService.deletePublisherbyId(publisherId, traceId);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
		

	}
		

}
