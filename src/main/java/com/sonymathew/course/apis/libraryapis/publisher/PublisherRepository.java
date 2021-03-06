package com.sonymathew.course.apis.libraryapis.publisher;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends CrudRepository<PublisherEntity, Integer>{
	
	List<PublisherEntity> findByName(String Name); 
	
	List<PublisherEntity> findByNameContaining(String Name);
	
	


}
