package com.sonymathew.course.apis.library.publisher;



import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.sonymathew.course.apis.library.exception.PublisherAlreadyExistsException;
import com.sonymathew.course.apis.library.exception.PublisherNotDeletedException;
import com.sonymathew.course.apis.library.exception.PublisherNotFoundException;
import com.sonymathew.course.apis.library.exception.PublisherNotUpdatedException;
import com.sonymathew.course.apis.library.utils.LibraryUtils;

@Service
public class PublisherService {
	
	private static Logger logger = LoggerFactory.getLogger(PublisherService.class);

	private PublisherRepository publisherRepository;

	
	public PublisherService(PublisherRepository publisherRepository) {
		this.publisherRepository = publisherRepository;
	}

	
	// Utility method to be used further downstream which creates Publisher Object from Publisher Entity
	private Publisher createPublisherFromEntity(PublisherEntity publisherEntity) {
		Publisher createdPublisherFromEntity = new Publisher(
												 publisherEntity.getPublisherid(),
												 publisherEntity.getName(),
												 publisherEntity.getEmailId(),
												 publisherEntity.getPhoneNumber()
												 );
		return createdPublisherFromEntity;
	}
	

	// Utility method to be used further downstream which creates Publisher Entity from Publisher Object
	private PublisherEntity createPublisherEntity(Publisher publisherObject) {
		// The publisher entity will be used to populate/update the DB table
		PublisherEntity publisherEntity = new PublisherEntity(
				publisherObject.getName(), 
				publisherObject.getEmailId(), 
				publisherObject.getPhoneNumber()
				);
		
		return  publisherEntity;
	}	

	
	
	// Add a new publisher entity
	public void addPublisher(Publisher publisherToBeAdded, String traceId)  
							 throws PublisherAlreadyExistsException {
		

		
		// Create publisher entity from publisher object in request
		PublisherEntity publisherEntity = createPublisherEntity(publisherToBeAdded);
		
		//This is the entity publisher that will used to save to the DB table using the publisher entity we just created above 
		PublisherEntity addedPublisherEntity = null;
		try{
			addedPublisherEntity = publisherRepository.save(publisherEntity);
			}catch(DataIntegrityViolationException divExc){
				// The below logging statement is refactored using log4j 
				//logger.error("Trace ID :*** " + traceId + " ***Duplicate...Publisher already exists, try another name!!!", divExc);
				logger.error("Trace ID :{},*** Duplicate...Publisher already exists, try another name!!!", traceId, divExc);
				throw new PublisherAlreadyExistsException("Trace ID :*** " + traceId + " ***Duplicate...Publisher already exists, try another name!!!");
		}
		
		//Now finally set the id of the publisher object
		publisherToBeAdded.setPublisherID(addedPublisherEntity.getPublisherid());
		logger.info("Trace ID : {}, Sucess !!! Publisher added: {}", traceId,publisherToBeAdded);
	
	}
	
	
	
	// Get the publisher entity by id 
	public Publisher getPublisherbyId(int publisherId, String traceId) throws PublisherNotFoundException {
		
		
		//This is the entity publisher used in the DB table. This is of type optional because we are using this to hold the results from the JPA API in PublisherRepository.class
		Optional<PublisherEntity> retrievedPublisher = null;
		
	
		 
		try{
			retrievedPublisher = publisherRepository.findById(publisherId);
		}catch(DataIntegrityViolationException divExc){
			// The below logging statement is refactored using log4j 
			// logger.error("Trace ID :*** " + traceId + " *** Publisher not Found!!Try with a different id...", divExc);
			logger.error("Trace ID :{}, *** Publisher not Found!!Try with a different id...", traceId, divExc);
			throw new PublisherNotFoundException("Trace ID :*** " + traceId + " *** Publisher not Found!!Try with a different id...");
			
		}
		
		// If the optional is valid then save to a Publisher entity object 
		if(retrievedPublisher.isPresent()) {
			PublisherEntity publisherEntity = retrievedPublisher.get();
			//Now create the publisher object from the retrieved entity.
			Publisher foundPublisher = createPublisherFromEntity(publisherEntity);
			logger.info("Trace ID : {}, Hurray !!! Publisher Found By ID: {}", traceId,foundPublisher);
			return foundPublisher;
		}else
		{   
			// The below logging statement is refactored using log4j 
			//logger.error("Trace ID :*** " + traceId + " *** Publisher not Found!!Try with a different id...");
			logger.error("Trace ID :{}, *** Publisher not Found!!Try with a different id...", traceId);
			throw new PublisherNotFoundException("Trace ID :*** " + traceId + " *** Publisher ID " + publisherId + " not Found!!Try with a different id...");
		}
			
	}




	// Get the publisher entity  by name - method 1 where full name is passed in the request path 
	public List<Publisher> getPublisherbyName(String publisherName, String traceId) throws PublisherNotFoundException {
		
		return findPublisherByName(publisherName,"FullName", traceId);
		
	}


	
	// Get the publisher entity  by name - method 2 where partial name is passed as a query in uri 
	public List<Publisher> searchPublisher(String publisherName, String traceId) {
		
		return findPublisherByName(publisherName,"PartialName",traceId);
	}
	

	
	//utility method 
	private List<Publisher> findPublisherByName(String publisherName, String searchMode, String traceId) {
		
		// Check if name is supplied
		if(!LibraryUtils.doesStringValueExist(publisherName)){
			// The below logging statement is refactored using log4j 
			//logger.error("Trace ID :*** " + traceId + " *** Publisher name not supplied !!!");
			logger.error("Trace ID :{}, *** Publisher name not supplied !!!", traceId);
			throw new PublisherNotFoundException("Trace ID :*** " + traceId + " *** Publisher name not supplied !!!");
		}
		
		List<PublisherEntity> publisherEntityList = new ArrayList<PublisherEntity>();
		
		//Entity list
		if(searchMode.matches("FullName")){
			publisherEntityList = publisherRepository.findByName(publisherName);
		}else{
			publisherEntityList = publisherRepository.findByNameContaining(publisherName);
		}
			
		
		//Object list
		//List<Publisher> publisherList = new ArrayList<>();
			
		
		
		if(publisherEntityList.isEmpty()){
			logger.error("Trace ID :{}, *** Publisher name " + publisherName + " not Found!!Try with different name...", traceId);
			throw new PublisherNotFoundException("Trace ID :*** " + traceId + " *** Publisher name " + publisherName + " not Found!!Try with different name...");
		}else{
			logger.info("Trace ID : {}, Gotcha by Name !!! Publisher Found By Name", traceId);
			return createPublishersForSearchResponse(publisherEntityList);
			/*
			 * this is the long winded method compared to the short one we have above
						
			for(PublisherEntity pube:publisherEntityList){
				publisherList.add(createPublisherFromEntity(pube));
	    		}
			
			return publisherList; 
			
			*/
			
		}
	}
	
	
	
	
	// utility method
	private List<Publisher> createPublishersForSearchResponse(List<PublisherEntity> publisherEntityList) {
		
		return publisherEntityList.stream()
								  .map(pube -> createPublisherFromEntity(pube))
								  .collect(Collectors.toList());
	}


	// Update the publisher entity  by id 
	public void updatePublisherbyId(Publisher publisherTobeUpdated, String traceId) throws PublisherNotFoundException {

		
		//This is the entity publisher used in the DB table. This is of type optional because we are using this to hold the results from teh JPA API in PublisherRepository.class
		Optional<PublisherEntity> retrievedPublisherEntity = null;
		
		
	
		// Retrieve the Publisher Entity from the DB table 
		try{
			retrievedPublisherEntity = publisherRepository.findById(publisherTobeUpdated.getPublisherID());
		}catch(DataIntegrityViolationException divExc){
			// The below logging statement is refactored using log4j 
			//logger.error("Trace ID :*** " + traceId + " *** Publisher ID " + publisherTobeUpdated.getPublisherID() + " not Found!!Try with a different id...",divExc);
			logger.error("Trace ID :{}, *** Publisher ID " + publisherTobeUpdated.getPublisherID() + "not Found!!Try with a different id..." , traceId, divExc);
			throw new PublisherNotFoundException("Trace ID :*** " + traceId + " *** Publisher ID " + publisherTobeUpdated.getPublisherID() + " not Found!!Try with a different id...");
			
		}
		
		
		// In case nothing is rertieved...Probably this check is redundant..
		if(!retrievedPublisherEntity.isPresent()){
			// The below logging statement is refactored using log4j 
			//logger.error("Trace ID :*** " + traceId + " *** Publisher ID " + publisherTobeUpdated.getPublisherID() + " not Found!!Try with a different id...");
			logger.error("Trace ID :{}, *** Publisher ID " + publisherTobeUpdated.getPublisherID() + "not Found!!Try with a different id..." , traceId);
			throw new PublisherNotFoundException("Trace ID :*** " + traceId + " *** Publisher ID " + publisherTobeUpdated.getPublisherID() + " not Found!!Try with a different id...");
		}	
		
		
		// Now if optional is valid , set the values of the Publisher entity in DB using values of the Publisher Object
		//map to publisher entity object 
		PublisherEntity publisherEntityMapped = retrievedPublisherEntity.get();
		
					
		//Now set the values in the mapped entity with those in the Request Publisher object
		boolean fieldChange  = false;
		
		
		// Check if the to be changed is valid and something actually has changed
		if(LibraryUtils.doesStringValueExist(publisherEntityMapped.getName()) &&  publisherEntityMapped.getName() != publisherTobeUpdated.getName()){
			publisherEntityMapped.setName(publisherTobeUpdated.getName());
			fieldChange  = true;
		}
		if(LibraryUtils.doesStringValueExist(publisherEntityMapped.getEmailId()) && publisherEntityMapped.getEmailId() != publisherTobeUpdated.getEmailId()){
			publisherEntityMapped.setEmailId(publisherTobeUpdated.getEmailId());
			fieldChange  = true;
		}
		if(LibraryUtils.doesStringValueExist(publisherEntityMapped.getPhoneNumber()) && publisherEntityMapped.getPhoneNumber() != publisherTobeUpdated.getPhoneNumber()){
			publisherEntityMapped.setPhoneNumber(publisherTobeUpdated.getPhoneNumber());
			fieldChange  = true;
		}
			
		//Finally update the database Publisher entity using the 'PublishRepository' interface 
		if(fieldChange){  // Only update if something has changed and is valid
			try{
				publisherRepository.save(publisherEntityMapped);
				logger.info("Trace ID : {}, Jolly...Updated the Publisher: {}", traceId,publisherEntityMapped);
				}catch(DataIntegrityViolationException divExc){
					// The below logging statement is refactored using log4j 
					//logger.error("Trace ID :*** " + traceId + " *** Blimey...something went wrong...contact admin and say SOS!!!",divExc);
					logger.error("Trace ID :{}, *** Blimey...something went wrong...contact admin and say SOS!!!" , traceId);
					throw new PublisherNotUpdatedException("Trace ID :*** " + traceId + " *** Blimey...something went wrong...contact admin and say SOS!!!");
				}
		}	
			
	}



	// Delete the publisher entity  by id 
	public void deletePublisherbyId(int publisherId, String traceId) {

		
		//This is the entity publisher used in the DB table. This is of type optional because we are using this to hold the results from teh JPA API in PublisherRepository.class
		Optional<PublisherEntity> retrievedPublisherEntity = null;
		
		
	
		// Retrieve the Publisher Entity from the DB table 
		try{
			retrievedPublisherEntity = publisherRepository.findById(publisherId);
		}catch(DataIntegrityViolationException divExc){
			// The below logging statement is refactored using log4j 
			//logger.error("Trace ID :*** " + traceId + " *** Publisher ID " + publisherId + " not Found!!Try with a different id...", divExc);
			logger.error("Trace ID :{}, *** Publisher ID " + publisherId + "not Found!!Try with a different id..." ,traceId);
			throw new PublisherNotFoundException("Trace ID :*** " + traceId + " *** Publisher ID " + publisherId + " not Found!!Try with a different id...");
			
		}
		
		
		// In case nothing is retrieved...Probably this check is redundant
		if(!retrievedPublisherEntity.isPresent()){
			// The below logging statement is refactored using log4j 
			//logger.error("Trace ID :*** " + traceId + " *** Publisher ID " + publisherId + " not Found!!Try with a different id...");
			logger.error("Trace ID :{}, *** Publisher ID " + publisherId + "not Found!!Try with a different id..." ,traceId);
			throw new PublisherNotFoundException("Trace ID :*** " + traceId + " *** Publisher ID " + publisherId + " not Found!!Try with a different id...");
		}	
		
		//map to publisher entity object 
		PublisherEntity publisherEntityToBeDeleted = retrievedPublisherEntity.get();
			
			
		//Finally delete the publisher entity from the database using the 'PublishRepository' interface 
		try{
			
			publisherRepository.delete(publisherEntityToBeDeleted);
			logger.info("Trace ID : {}, Target terminated: {}", traceId,publisherEntityToBeDeleted);
			}catch(DataIntegrityViolationException divExc){
				// The below logging statement is refactored using log4j 
				//logger.error("Trace ID :*** " + traceId + " *** Will die another day...Get professional help!!!",divExc);
				logger.error("Trace ID :{}, *** *** Will die another day...Get professional help!!!" ,traceId,divExc);
				throw new PublisherNotDeletedException("Trace ID :*** " + traceId + " *** Will die another day...Get professional help!!!");
		}
			
			
	}

	

	
}	

