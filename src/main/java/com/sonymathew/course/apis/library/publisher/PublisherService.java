package com.sonymathew.course.apis.library.publisher;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.sonymathew.course.apis.library.exception.LibraryResourceAlreadyExistsException;

@Service
public class PublisherService {
	

	private PublisherRepository publisherRepository;

	
	public PublisherService(PublisherRepository publisherRepository) {
		this.publisherRepository = publisherRepository;
	}


	public Publisher addPublisher(Publisher publisherToBeAdded) throws LibraryResourceAlreadyExistsException {
		
		PublisherEntity publisherEntity = new PublisherEntity(
											publisherToBeAdded.getName(), 
											publisherToBeAdded.getEmailId(), 
											publisherToBeAdded.getPhoneNumber()
											);
		
		
		//This is the entity publisher used in the DB table 
		PublisherEntity addedPublisher = null;
		
		try{
			addedPublisher = publisherRepository.save(publisherEntity);
		}catch(DataIntegrityViolationException divExc){
			throw new LibraryResourceAlreadyExistsException("Publisher already exists!!!");
			
		}
		
		//Thsi is the object publisher
		publisherToBeAdded.setPublisherID(addedPublisher.getPublisherid());
		return publisherToBeAdded;
		
	}
	
}	

