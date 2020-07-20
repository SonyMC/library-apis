package com.sonymathew.course.apis.library.publisher;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	public ResponseEntity<?> getPublisherbyId(@PathVariable Integer publisherId){
		
		// Note : The exception handling has been moved out to the service class 
		Publisher publisherbyId = publisherService.getPublisherbyId(publisherId);
		return new ResponseEntity<>(publisherbyId,HttpStatus.FOUND);
		

	}
	
	
	// Get publisher by name
	@GetMapping(path = "/name/{publisherName}")
	public ResponseEntity<?> getPublisherByName(@PathVariable String publisherName){
		
		// Note : The exception handling has been moved out to the service class 
		List<Publisher> publisherList = publisherService.getPublisherbyName(publisherName);
		return new ResponseEntity<>(publisherList,HttpStatus.FOUND);
		

	}
		
	// Search publisher by name provided in uri query; Note that instead of @PathVariable in parm we use @RequestParam
	@GetMapping(path = "/search")
	public ResponseEntity<?> searchPublisherByName(@RequestParam String publisherName){
		
		//Check that name query is provided
		if(!LibraryUtils.doesStringValueExist(publisherName)){
			return new ResponseEntity<>("Please provide name to be searched as a query in the uri  ", HttpStatus.BAD_REQUEST);
		}
		// Note : The exception handling has been moved out to the service class 
		List<Publisher> publisherList = publisherService.searchPublisher(publisherName);
		return new ResponseEntity<>(publisherList,HttpStatus.FOUND);
		

	}	
	
	// Create a new publisher 
	@PostMapping
	public ResponseEntity<?> addPublisher(@RequestBody Publisher publisher){

		// Note : The exception handling has been moved out to the service class 
		publisherService.addPublisher(publisher);
		return new ResponseEntity<>(publisher, HttpStatus.CREATED);
	}
	
	
	//Update a Publisher
	@PutMapping(path = "/{publisherId}")
	public ResponseEntity<?> UpdatePublisherbyId(@PathVariable Integer publisherId, @RequestBody Publisher publisherTobeUpdated){
		
		// We need to set the publisher id with what has been supplied in the path ,else whatever publisher is passed in parm will be updated.
		publisherTobeUpdated.setPublisherID(publisherId);
		// Note : The exception handling has been moved out to the service class 
		publisherService.updatePublisherbyId(publisherTobeUpdated);
		return new ResponseEntity<>(publisherTobeUpdated,HttpStatus.OK);
		

	}
	
	// Delete publisher by id 
	@DeleteMapping(path = "/{publisherId}")
	public ResponseEntity<?> deletePublisherbyId(@PathVariable Integer publisherId){
		
		// Note : The exception handling has been moved out to the service class 
		publisherService.deletePublisherbyId(publisherId);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
		

	}
		

}
