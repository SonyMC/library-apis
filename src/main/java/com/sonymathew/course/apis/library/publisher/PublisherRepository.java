package com.sonymathew.course.apis.library.publisher;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends CrudRepository<PublisherEntity, Integer>{
	
	List<PublisherEntity> findByName(String Name); 


}
