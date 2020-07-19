package com.sonymathew.course.apis.library.publisher;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.sonymathew.course.apis.library.exception.PublisherAlreadyExistsException;
import com.sonymathew.course.apis.library.exception.PublisherNotDeletedException;
import com.sonymathew.course.apis.library.exception.PublisherNotFoundException;
import com.sonymathew.course.apis.library.exception.PublisherNotUpdatedException;
import com.sonymathew.course.apis.library.utils.LibraryUtils;

@Service
public class PublisherService {
	

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
	public void addPublisher(Publisher publisherToBeAdded) throws PublisherAlreadyExistsException {
		
		// Create publisher entity from publisher object in request
		PublisherEntity publisherEntity = createPublisherEntity(publisherToBeAdded);
		
		//This is the entity publisher that will used to save to the DB table using the publisher entity we just created above 
		PublisherEntity addedPublisherEntity = null;
		try{
			addedPublisherEntity = publisherRepository.save(publisherEntity);
			}catch(DataIntegrityViolationException divExc){
			throw new PublisherAlreadyExistsException("Duplicate...Publisher already exists, try another name!!!");
		}
		
		//Now finally set the id of the publisher object
		publisherToBeAdded.setPublisherID(addedPublisherEntity.getPublisherid());
	
	}
	
	
	
	// Get the publisher entity by id 
	public Publisher getPublisherbyId(int publisherId) throws PublisherNotFoundException {
		
		
		//This is the entity publisher used in the DB table. This is of type optional because we are using this to hold the results from the JPA API in PublisherRepository.class
		Optional<PublisherEntity> retrievedPublisher = null;
		
	
		 
		try{
			retrievedPublisher = publisherRepository.findById(publisherId);
		}catch(DataIntegrityViolationException divExc){
			throw new PublisherNotFoundException("Publisher not Found!!Try with a different id...");
			
		}
		
		// If the optional is valid then save to a Publisher entity object 
		if(retrievedPublisher.isPresent()) {
			PublisherEntity publisherEntity = retrievedPublisher.get();
			//Now create the publisher object from the retrieved entity.
			Publisher foundPublisher = createPublisherFromEntity(publisherEntity);
			return foundPublisher;
		}else
		{
			throw new PublisherNotFoundException("Publisher ID " + publisherId + " not Found!!Try with a different id...");
		}
			
	}




	// Get the publisher entity  by name
	public List<Publisher> getPublisherbyName(String publisherName) throws PublisherNotFoundException {
		
		//Entity list
		List<PublisherEntity> publisherEntityList = publisherRepository.findByName(publisherName);
		
		//Object list
		List<Publisher> publisherList = new ArrayList<>();
			
		
		
		if(publisherEntityList.isEmpty()){
			throw new PublisherNotFoundException("Publisher name " + publisherName + " not Found!!Try with different name...");
		}else{
			for(PublisherEntity pube:publisherEntityList){
				publisherList.add(createPublisherFromEntity(pube));
	    		}
			
		return publisherList;
			
		}
		
	}
	
	// Update the publisher entity  by id 
	public void updatePublisherbyId(Publisher publisherTobeUpdated) throws PublisherNotFoundException {

		
		//This is the entity publisher used in the DB table. This is of type optional because we are using this to hold the results from teh JPA API in PublisherRepository.class
		Optional<PublisherEntity> retrievedPublisherEntity = null;
		
		
	
		// Retrieve the Publisher Entity from the DB table 
		try{
			retrievedPublisherEntity = publisherRepository.findById(publisherTobeUpdated.getPublisherID());
		}catch(DataIntegrityViolationException divExc){
			throw new PublisherNotFoundException("Publisher ID " + publisherTobeUpdated.getPublisherID() + " not Found!!Try with a different id...");
			
		}
		
		
		// In case nothing is rertieved...Probably this check is redundant..
		if(!retrievedPublisherEntity.isPresent()){
			throw new PublisherNotFoundException("Publisher ID " + publisherTobeUpdated.getPublisherID() + " not Found!!Try with a different id...");
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
				}catch(DataIntegrityViolationException divExc){
					throw new PublisherNotUpdatedException("Blimey...something went wrong...contact admin and say SOS!!!");
				}
		}	
			
	}



	// Delete the publisher entity  by id 
	public void deletePublisherbyId(int publisherId) {

		
		//This is the entity publisher used in the DB table. This is of type optional because we are using this to hold the results from teh JPA API in PublisherRepository.class
		Optional<PublisherEntity> retrievedPublisherEntity = null;
		
		
	
		// Retrieve the Publisher Entity from the DB table 
		try{
			retrievedPublisherEntity = publisherRepository.findById(publisherId);
		}catch(DataIntegrityViolationException divExc){
			throw new PublisherNotFoundException("Publisher ID " + publisherId + " not Found!!Try with a different id...");
			
		}
		
		
		// In case nothing is retrieved...Probably this check is redundant
		if(!retrievedPublisherEntity.isPresent()){
			throw new PublisherNotFoundException("Publisher ID " + publisherId + " not Found!!Try with a different id...");
		}	
		
		//map to publisher entity object 
		PublisherEntity publisherEntityToBeDeleted = retrievedPublisherEntity.get();
			
			
		//Finally delete the publisher entity from the database using the 'PublishRepository' interface 
		try{
			publisherRepository.delete(publisherEntityToBeDeleted);
			}catch(DataIntegrityViolationException divExc){
				throw new PublisherNotDeletedException("Will die another day...Get professional help!!!");
		}
			
			
	}
	
}	

