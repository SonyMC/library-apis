package com.sonymathew.course.apis.library.publisher;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.sonymathew.course.apis.library.exception.PublisherAlreadyExistsException;
import com.sonymathew.course.apis.library.exception.PublisherNotFoundException;

@Service
public class PublisherService {
	

	private PublisherRepository publisherRepository;

	
	public PublisherService(PublisherRepository publisherRepository) {
		this.publisherRepository = publisherRepository;
	}

	
	private Publisher createPublisherFromEntity(PublisherEntity publisherEntity) {
		Publisher createdPublisherFromEntity = new Publisher(
												 publisherEntity.getPublisherid(),
												 publisherEntity.getName(),
												 publisherEntity.getEmailId(),
												 publisherEntity.getPhoneNumber()
												 );
		return createdPublisherFromEntity;
	}
	

	public void addPublisher(Publisher publisherToBeAdded) throws PublisherAlreadyExistsException {
		
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
			throw new PublisherAlreadyExistsException("Publisher already exists!!!");
			
		}
		
		//This is the object publisher
		publisherToBeAdded.setPublisherID(addedPublisher.getPublisherid());

		
	}
	
	
	public Publisher getPublisherbyId(int publisherId) throws PublisherNotFoundException {
		
		
		//This is the entity publisher used in the DB table 
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
			//Publisher foundPublisher = new Publisher(publisherID, name, emailId, phoneNumber)
			
			Publisher foundPublisher = createPublisherFromEntity(publisherEntity);
			return foundPublisher;
		}else
		{
			throw new PublisherNotFoundException("Publisher ID " + publisherId + " not Found!!Try with a different id...");
		}
			
	}





	public List<Publisher> getPublisherbyName(String publisherName) throws PublisherNotFoundException {
		
		//Entity list
		List<PublisherEntity> publisherEntityList = publisherRepository.findByName(publisherName);
		
		//Object list
		List<Publisher> publisherList = new ArrayList<>();
			
		
		
		if(publisherEntityList.isEmpty()){
			throw new PublisherNotFoundException("Publisher name " + publisherName + " not Found!!Try with different name...");
		}else{
			for(PublisherEntity pube:publisherEntityList){
				publisherList.add(new Publisher(
												pube.getPublisherid(),
												pube.getName(),
												pube.getEmailId(),
												pube.getPhoneNumber()
	     									));
				
	    		}
			
		return publisherList;
			
		}
		
	}

	
}	

