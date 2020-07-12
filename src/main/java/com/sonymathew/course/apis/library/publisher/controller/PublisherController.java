package com.sonymathew.course.apis.library.publisher.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sonymathew.course.apis.publisher.model.Publisher;

@RestController
@RequestMapping(path="/v1/publishers")
public class PublisherController {
	
	
//	@GetMapping(path="/v1/publishers")
//	public String HelloWorld(){
//		return ("Welcome To World of Awesome Printing By Prentice!!!");
//	}
//	
	
	@GetMapping(path = "/{publisherId}")
	public Publisher getPublisher(@PathVariable String publisherId){
		return new Publisher(publisherId, "PrenticeHall Publishers", "prentice@email.com", "12345");
	}

}
