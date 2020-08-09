package com.sonymathew.course.apis.library.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.sonymathew.course.apis.libraryapis.publisher.Publisher;
import com.sonymathew.course.apis.libraryapis.publisher.PublisherEntity;

public class LibraryApiTestUtil {
	
	public static Publisher createPublisher(){
		return new Publisher(null,
							 TestConstants.TEST_PUBLISHER_NAME,
							 TestConstants.TEST_PUBLISHER_EMAIL, 
							 TestConstants.TEST_PUBLISHER_PHONE);
		
	}

	public static PublisherEntity createPublisherEntity() {
		return new PublisherEntity(
				 TestConstants.TEST_PUBLISHER_NAME,
				 TestConstants.TEST_PUBLISHER_EMAIL, 
				 TestConstants.TEST_PUBLISHER_PHONE);

	}

	public static Optional<PublisherEntity> createPublisherEntityOptional() {
		return Optional.of(createPublisherEntity());
	
	}

	public static List<PublisherEntity> getPublisherbyName() {
		List<PublisherEntity> publisherEntityList = new ArrayList<PublisherEntity>();
		publisherEntityList.add(createPublisherEntity());
		return publisherEntityList;
	}

}
