package com.sonymathew.course.apis.library;

/* Notes:
 * (1) In Eclipse , when one creates Jinit test classes, * there will not be any setup method auto created.
 *  Instead we have a @BeforeClass method. Remove this and create a new mehod with @Before annotation ( any name will do but better to use setUp() or initMock()  .
 *   Else you will get a null pointer exception.
 *
 *  (2) For the mockito method verify(publisherRepository, times(1)).save((any(PublisherEntity.class))) 
 *  mentioned in teg htutorial, you have to static import org.mockito.ArgumentMatchers.any; 
 *  for the 'any' function to work. Else you will get a typecast error which even if you cast will produce a runtime error.
 */

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;

import com.sonymathew.course.apis.library.utils.old.LibraryApiTestUtil;
import com.sonymathew.course.apis.library.utils.old.TestConstants;
import com.sonymathew.course.apis.libraryapis.exception.PublisherAlreadyExistsException;
import com.sonymathew.course.apis.libraryapis.exception.PublisherNotDeletedException;
import com.sonymathew.course.apis.libraryapis.exception.PublisherNotFoundException;
import com.sonymathew.course.apis.libraryapis.exception.PublisherNotUpdatedException;
import com.sonymathew.course.apis.libraryapis.publisher.Publisher;
import com.sonymathew.course.apis.libraryapis.publisher.PublisherEntity;
import com.sonymathew.course.apis.libraryapis.publisher.PublisherRepository;
import com.sonymathew.course.apis.libraryapis.publisher.PublisherService;



@RunWith(MockitoJUnitRunner.class)
public class PublisherServiceTest {
	
	@Mock
	static PublisherRepository publisherRepository;
	
	
	static PublisherService publisherService;
	
	@Before
	public void setUp() throws Exception {
		publisherService = new PublisherService(publisherRepository);
	}



	@Test
	public void testPublisherService() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddPublisher_success() throws PublisherAlreadyExistsException {
		
		
		// Note : 'when' function here is mockito provided .
		//Essentially what we are saying here is that whenever the save method is called for the repository, then return the test entity from the util.  
		when(publisherRepository.save(any(PublisherEntity.class)))
		.thenReturn(LibraryApiTestUtil.createPublisherEntity());
			
		Publisher publisher = LibraryApiTestUtil.createPublisher();
		publisherService.addPublisher(publisher, TestConstants.API_TRACE_ID);
		
		/// Now using mockito verify and junit assert , we will see if save is called in the publisher service
		//  The below method verfies that the save method in the Repositoty is called once for the for the Publisher Entity 
		verify(publisherRepository, times(1)).save((any(PublisherEntity.class)));
		assertNotNull(publisher.getPublisherID());  // publisher id is auto generated
		assertTrue(publisher.getName().equals(TestConstants.TEST_PUBLISHER_NAME)); // publisher is same as the test constant publisher name 
		
	}

	
	
	@Test(expected = PublisherAlreadyExistsException.class)
	public void testAddPublisher_failure() throws PublisherAlreadyExistsException {
		
		
		// Note : Ask Mockito to throw an error when something is attempted to be saved in the repository
		doThrow(DataIntegrityViolationException.class).when(publisherRepository).save(any(PublisherEntity.class));
			
		Publisher publisher = LibraryApiTestUtil.createPublisher();
		publisherService.addPublisher(publisher, TestConstants.API_TRACE_ID);
		
		/// Now using mockito verify and junit assert , we will see if save is called in the publisher service
		//  The below method verfies that the save method in the Repository is called once for the for the Publisher Entity 
		verify(publisherRepository, times(1)).save((any(PublisherEntity.class)));
		
		
	}
	
	@Test
	public void testGetPublisherbyId_success() throws PublisherNotFoundException {
		
		// Note : Ask Mockito to return an optional entity when any publisher is searched by id 
		when(publisherRepository.findById(anyInt()))
		.thenReturn(LibraryApiTestUtil.createPublisherEntityOptional());
		
		Publisher publisher = publisherService.getPublisherbyId(123, TestConstants.API_TRACE_ID);
		
		// use mockito verify 	
		verify(publisherRepository, times(1)).findById(123);
		// assert statements are from junit
		assertNotNull(publisher);  
		assertNotNull(publisher.getPublisherID());
	}
	
	@Test(expected = PublisherNotFoundException.class)
	public void testGetPublisherbyId_failure() throws PublisherNotFoundException {
		
		/// Note : Ask Mockito to throw an error when a search is made on any int
		doThrow(DataIntegrityViolationException.class).when(publisherRepository).findById(anyInt());
		
		// In error publisher will not be returned and hence no need for destination object
		publisherService.getPublisherbyId(123, TestConstants.API_TRACE_ID);
		
		// use mockito verify 	
		verify(publisherRepository, times(1)).findById(123);
		

	}	

	@Test
	public void testGetPublisherbyName_success() throws PublisherNotFoundException{
	
	// Note : Ask Mockito to return an entity when any publisher is searched by name
	when(publisherRepository.findByName(anyString()))
	.thenReturn(LibraryApiTestUtil.getPublisherbyName());
	
	
	List<Publisher> publisherList = publisherService.getPublisherbyName(TestConstants.TEST_PUBLISHER_NAME, TestConstants.API_TRACE_ID);
	
	// use mockito verify 	
	verify(publisherRepository, times(1)).findByName(TestConstants.TEST_PUBLISHER_NAME);
	// assert statements are from junit
	assertNotNull(publisherList);
	
	// assert each Publisherin the list is valid
	for(Publisher pub:publisherList){
		assertNotNull(pub.getPublisherID());
		}
	
	}

	
	@Test(expected = DataIntegrityViolationException.class)
	public void testGetPublisherbyName_failure() throws PublisherNotFoundException{
	
	// Note : Ask Mockito to throw an error when a search is made on any name
	doThrow(DataIntegrityViolationException.class).when(publisherRepository).findByName(anyString());	

	
	// In error publisher will not be returned and hence no need for destination object
	publisherService.getPublisherbyName(TestConstants.TEST_PUBLISHER_NAME, TestConstants.API_TRACE_ID);
	
	// use mockito verify 	
	verify(publisherRepository, times(1)).findByName(TestConstants.TEST_PUBLISHER_NAME);

		
	}	
	
	
	@Test
	public void testSearchPublisher_success() throws PublisherNotFoundException {
		

	
	// Note : Ask Mockito to return an entity when any publisher is searched by name
	when(publisherRepository.findByNameContaining(anyString()))
	.thenReturn(LibraryApiTestUtil.getPublisherbyName());
	
	
	List<Publisher> publisherList = publisherService.searchPublisher(TestConstants.TEST_PUBLISHER_NAME, TestConstants.API_TRACE_ID);
	
	// use mockito verify 	
	verify(publisherRepository, times(1)).findByNameContaining(TestConstants.TEST_PUBLISHER_NAME);
	// assert statements are from junit
	assertNotNull(publisherList);
	
	// assert each Publisherin the list is valid
	for(Publisher pub:publisherList){
		assertNotNull(pub.getPublisherID());
		}
		

	}
	
	
	@Test(expected=DataIntegrityViolationException.class)
	public void testSearchPublisher_failure() throws PublisherNotFoundException {
		
	
	// Note : Ask Mockito to throw an error when a search is made on any name
	doThrow(DataIntegrityViolationException.class).when(publisherRepository).findByNameContaining(anyString());	
	
	
	// In error publisher will not be returned and hence no need for destination object
	 publisherService.searchPublisher(TestConstants.TEST_PUBLISHER_NAME, TestConstants.API_TRACE_ID);
	
	// use mockito verify 	
	 verify(publisherRepository, times(1)).findByNameContaining(TestConstants.TEST_PUBLISHER_NAME);

	}	

	
	
	
	@Test
	public void testUpdatePublisherbyId_success() throws PublisherNotFoundException, PublisherNotUpdatedException, PublisherAlreadyExistsException {

		
	
	   	// Note : Ask Mockito to return a Publisher entity when any publisher is updated by id
		when(publisherRepository.save(any(PublisherEntity.class)))
		.thenReturn(LibraryApiTestUtil.createPublisherEntity());
		
		// Step 1. Create Publisher object and add to repository 
		Publisher publisher = LibraryApiTestUtil.createPublisher();
		//Add publisher object to repo  
		publisherService.addPublisher(publisher, TestConstants.API_TRACE_ID);
		
		//Step 2: Verify object is saved in repo :  Now using mockito verify  , we will see if save is called in the publisher service
		verify(publisherRepository, times(1)).save((any(PublisherEntity.class)));
		
		// Step 3: Update publisher oject now update the publisher object email id and phone number
		publisher.setEmailId(TestConstants.TEST_PUBLISHER_EMAIL_UPDATED);
		publisher.setPhoneNumber(TestConstants.TEST_PUBLISHER_PHONE_UPDATED);
		
		//Steo 4 : Before saving to repo, ask mockito to retrieve the the optional entity to retrieve teh optional entity from repo 
		when(publisherRepository.findById(anyInt())).thenReturn(LibraryApiTestUtil.createPublisherEntityOptional());
		
		
		// Step 5. Update thepublisher in the repo 
		publisherService.updatePublisherbyId(publisher, TestConstants.API_TRACE_ID);
    	
		
		// Step 6. use mockito verify teh find by id was successful
		verify(publisherRepository,times(1)).findById(publisher.getPublisherID());
		
		// Step 7. finally use mockito verify the updated entity is saved to the repo. The invocation for save is teh second in thsi block.
		verify(publisherRepository, times(2)).save((any(PublisherEntity.class)));
		
			
		//Step 8: Junit to assert 
		assertTrue(TestConstants.TEST_PUBLISHER_EMAIL_UPDATED.equals(publisher.getEmailId()));
		assertTrue(TestConstants.TEST_PUBLISHER_PHONE_UPDATED.equals(publisher.getPhoneNumber()));
	}
	
/*	
	@Test(expected= DataIntegrityViolationException.class)
	public void testUpdatePublisherbyId_failure() throws  PublisherNotFoundException, PublisherNotUpdatedException, PublisherAlreadyExistsException {

		
		// Note : Ask Mockito to throw an error when a save is made
		doThrow(DataIntegrityViolationException.class).when(publisherRepository).save(any(PublisherEntity.class));	
		
	
		// Step 1. Create Publisher object and add to repository 
		Publisher publisher = LibraryApiTestUtil.createPublisher();
		//Add publisher object to repo  
		publisherService.addPublisher(publisher, TestConstants.API_TRACE_ID);
		
		//Step 2: Verify object is saved in repo :  Now using mockito verify  , we will see if save is called in the publisher service
		verify(publisherRepository, times(1)).save((any(PublisherEntity.class)));
		
		// Step 3: Update publisher oject now update the publisher object email id and phone number
		publisher.setEmailId(TestConstants.TEST_PUBLISHER_EMAIL_UPDATED);
		publisher.setPhoneNumber(TestConstants.TEST_PUBLISHER_PHONE_UPDATED);
		
				
		// Step 4:  : Ask Mockito to throw an error when a retireve is made from repo 
		doThrow(DataIntegrityViolationException.class).when(publisherRepository).findById(anyInt());	

		// Step 5. Update thepublisher in the repo 
		publisherService.updatePublisherbyId(publisher, TestConstants.API_TRACE_ID);
    	
		
		// Step 6. use mockito verify teh find by id was successful
		verify(publisherRepository,times(1)).findById(publisher.getPublisherID());
		
		// Step 7. finally use mockito verify the updated entity is saved to the repo. The invocation for save is teh second in thsi block.
		verify(publisherRepository, times(2)).save((any(PublisherEntity.class)));
		

	}
*/	

	@Test
	public void testDeletePublisherbyId_success() throws PublisherNotFoundException, PublisherNotDeletedException, PublisherAlreadyExistsException, PublisherNotUpdatedException {
		
	
		
	   	// Step 1 : Ask Mockito to return a Publisher entity when any publisher is created
    	when(publisherRepository.save(any(PublisherEntity.class)))
		.thenReturn(LibraryApiTestUtil.createPublisherEntity());
		
		// Step 2. Create Publisher object and add to repository 
		Publisher publisher = LibraryApiTestUtil.createPublisher();
		//Add publisher object to repo  
		publisherService.addPublisher(publisher, TestConstants.API_TRACE_ID);
		
		//Step 3: Verify object is saved in repo :  Now using mockito verify  , we will see if save is called in the publisher service
		verify(publisherRepository, times(1)).save((any(PublisherEntity.class)));
		
				
		//Steo 4 : Mockito will do nothing on  a delete 
		doNothing().when(publisherRepository).deleteById(publisher.getPublisherID());
		
	
		// Step 5. delete by publisher id 
		publisherService.deletePublisherbyId(publisher.getPublisherID(), TestConstants.API_TRACE_ID);
   	
	
	    // Step 6. use mockito verify the delete by id was successful
		verify(publisherRepository,times(1)).deleteById(publisher.getPublisherID());
		
				

		
	}
	
	
	@Test(expected=PublisherNotFoundException.class)
	public void testDeletePublisherbyId_failure() throws PublisherNotFoundException, PublisherNotDeletedException, PublisherAlreadyExistsException, PublisherNotUpdatedException {
		
		// Note : ask Mockito to throw an exception
		doThrow(DataIntegrityViolationException.class).when(publisherRepository).deleteById(123);
		
	
		// delete by publisher id 
		publisherService.deletePublisherbyId(123, TestConstants.API_TRACE_ID);
		
		//mockito verify
		verify(publisherRepository,times(1)).deleteById(123);
	}
		

}
