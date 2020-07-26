package com.sonymathew.course.apis.library.publisher;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

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

import com.sonymathew.course.apis.library.utils.LibraryUtils;


@RestController
@RequestMapping(path="/v1/publishers")
public class PublisherController {
	
@Autowired	
private PublisherService publisherService;


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
			  								  @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId){
		
		
		// Now check if trace id is provided in request by consumer. IF not, we will generate it 
		if(!LibraryUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}
		
		
		// Note : The exception handling has been moved out to the service class 
		Publisher publisherbyId = publisherService.getPublisherbyId(publisherId, traceId);
		return new ResponseEntity<>(publisherbyId,HttpStatus.FOUND);
		

	}
	
	
	// Get publisher by name
	@GetMapping(path = "/name/{publisherName}")
	public ResponseEntity<?> getPublisherByName(@PathVariable String publisherName,
												@RequestHeader(value = "Trace-Id", defaultValue = "") String traceId){
		
		
		// Now check if trace id is provided in request by consumer. IF not, we will generate it 
		if(!LibraryUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}		
		
		// Note : The exception handling has been moved out to the service class 
		List<Publisher> publisherList = publisherService.getPublisherbyName(publisherName, traceId);
		return new ResponseEntity<>(publisherList,HttpStatus.FOUND);
		

	}
		
	// Search publisher by name provided in uri query; Note that instead of @PathVariable in parm we use @RequestParam
	@GetMapping(path = "/search")
	public ResponseEntity<?> searchPublisherByName(@RequestParam String publisherName,
												   @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId){
		
		

		//Check that name query is provided
		if(!LibraryUtils.doesStringValueExist(publisherName)){
			return new ResponseEntity<>("Please provide name to be searched as a query in the uri  ", HttpStatus.BAD_REQUEST);
		}
		
		// Now check if trace id is provided in request by consumer. IF not, we will generate it 
		if(!LibraryUtils.doesStringValueExist(traceId)){
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
										  @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId){

		// Now check if trace id is provided in request by consumer. IF not, we will generate it 
		if(!LibraryUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}	
		
		// Note : The exception handling has been moved out to the service class 
		publisherService.addPublisher(publisher, traceId);
		return new ResponseEntity<>(publisher, HttpStatus.CREATED);
	}
	
	
	//Update a Publisher
	@PutMapping(path = "/{publisherId}")
	public ResponseEntity<?> UpdatePublisherbyId(@PathVariable Integer publisherId,
												 @ Valid @RequestBody Publisher publisherTobeUpdated,
												 @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId){
		
		
		// Now check if trace id is provided in request by consumer. IF not, we will generate it 
		if(!LibraryUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}
		
		// We need to set the publisher id with what has been supplied in the path ,else whatever publisher is passed in parm will be updated.
		publisherTobeUpdated.setPublisherID(publisherId);
		// Note : The exception handling has been moved out to the service class 
		publisherService.updatePublisherbyId(publisherTobeUpdated, traceId);
		return new ResponseEntity<>(publisherTobeUpdated,HttpStatus.OK);
		

	}
	
	// Delete publisher by id 
	@DeleteMapping(path = "/{publisherId}")
	public ResponseEntity<?> deletePublisherbyId(@PathVariable Integer publisherId,
			 									 @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId){
		
		// Now check if trace id is provided in request by consumer. IF not, we will generate it 
		if(!LibraryUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}
		
		// Note : The exception handling has been moved out to the service class 
		publisherService.deletePublisherbyId(publisherId, traceId);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
		

	}
		

}
