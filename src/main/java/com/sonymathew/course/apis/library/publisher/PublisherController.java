package com.sonymathew.course.apis.library.publisher;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sonymathew.course.apis.library.exception.LibraryResourceAlreadyExistsException;
import com.sonymathew.course.apis.library.exception.PublisherAlreadyExistsException;

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
	
	@GetMapping(path = "/{publisherId}")
	public ResponseEntity<?> getPublisherbyId(@PathVariable Integer publisherId){
		
		// Note : The exception handlign has been moved out to the service class 
		Publisher publisherbyId = publisherService.getPublisherbyId(publisherId);
		return new ResponseEntity<>(publisherbyId,HttpStatus.FOUND);
		

	}
	
	@GetMapping(path = "/name/{publisherName}")
	public ResponseEntity<?> getPublisherByName(@PathVariable String publisherName){
		
		// Note : The exception handlign has been moved out to the service class 
		List<Publisher> publisherList = publisherService.getPublisherbyName(publisherName);
		return new ResponseEntity<>(publisherList,HttpStatus.FOUND);
		

	}
		
	
	@PostMapping
	public ResponseEntity<?> addPublisher(@RequestBody Publisher publisher){

		// Note : The exception handlign has been moved out to the service class 
		publisherService.addPublisher(publisher);
		return new ResponseEntity<>(publisher, HttpStatus.CREATED);
	}

}
