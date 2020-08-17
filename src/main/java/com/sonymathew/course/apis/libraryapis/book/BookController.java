package com.sonymathew.course.apis.libraryapis.book;

import java.util.List;
import java.util.Set;
import java.util.UUID;


import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sonymathew.course.apis.libraryapis.exception.LibraryResourceUnauthorizedException;
import com.sonymathew.course.apis.libraryapis.exception.PublisherNotFoundException;
import com.sonymathew.course.apis.libraryapis.exception.BookAlreadyExistsException;
import com.sonymathew.course.apis.libraryapis.exception.BookBadRequestException;
import com.sonymathew.course.apis.libraryapis.exception.BookNotDeletedException;
import com.sonymathew.course.apis.libraryapis.exception.BookNotFoundException;
import com.sonymathew.course.apis.libraryapis.exception.BookNotUpdatedException;
import com.sonymathew.course.apis.libraryapis.exception.LibraryResourceNotFoundException;
import com.sonymathew.course.apis.libraryapis.utils.LibraryApiUtils;


@RestController
@RequestMapping(path="/v1/books")
public class BookController {
	
@Autowired	
private BookService bookService;


private static Logger logger = LoggerFactory.getLogger(BookController.class);

public BookController(BookService bookService) {
	this.bookService = bookService;
}


//	@GetMapping(path="/v1/books")
//	public String HelloWorld(){
//		return ("Welcome To World of Awesome Printing By Prentice!!!");
//	}
//	


	// Get book by id 
	@GetMapping(path = "/bookId/{bookId}")
	public ResponseEntity<?> getBookbyId(@PathVariable Integer bookId,
			  								  @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId) throws BookNotFoundException{
		
		
		
		
		// Now check if trace id is provided in request by consumer. fF not, we will generate it 
		if(!LibraryApiUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}
		
		logger.info("Trace ID : {}, Request to retrieve Book By ID: {}", traceId, bookId);
		
		// Note : The exception handling has been moved out to the service class 
		Book bookbyId = bookService.getBookbyId(bookId, traceId);
		return new ResponseEntity<>(bookbyId,HttpStatus.FOUND);
		

	}
	
	// Get book by isbn 
	@GetMapping(path = "/isbn/{isbn}")
	public ResponseEntity<?> getBookbyIsbn(@PathVariable String isbn,
			  								  @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId) throws BookNotFoundException{
		
		
		
		
		// Now check if trace id is provided in request by consumer. If not, we will generate it 
		if(!LibraryApiUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}
		
		logger.info("Trace ID : {}, Request to retrieve Book By Isbn: {}", traceId, isbn);
		
		// Note : The exception handling has been moved out to the service class 
		Book bookByIsbn = bookService.getBookbyIsbn(isbn, traceId);
		return new ResponseEntity<>(bookByIsbn,HttpStatus.FOUND);
		

	}
		
	
	
	// Get book by name
	@GetMapping(path = "/title/{title}")
	public ResponseEntity<?> getBookByTitle(@PathVariable String title,
												@RequestHeader(value = "Trace-Id", defaultValue = "") String traceId) throws BookNotFoundException{
		
		
		// Now check if trace id is provided in request by consumer. IF not, we will generate it 
		if(!LibraryApiUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}	
		
		logger.info("Trace ID : {}, Request to retrieve Book By Name : {}", traceId, title);
		
		// Note : The exception handling has been moved out to the service class 
		List<Book> bookList = bookService.getBookbyTitle(title, traceId);
		return new ResponseEntity<>(bookList,HttpStatus.FOUND);
		

	}
		

	// Search book by isbn provided in uri query; Note that instead of @PathVariable in parm we use @RequestParam
	@GetMapping(path = "/search")
	public ResponseEntity<?> searchBookByTitle(@RequestParam String title,
												   @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId) throws BookNotFoundException, BookBadRequestException{
		
		

		//Check that name query is provided
		if(!LibraryApiUtils.doesStringValueExist(title)){
			
			// the commented our Response entity exception message is now replaced by teh curomized exception handler class/method 
			//return new ResponseEntity<>("Please provide title to be searched as a query in the uri  ", HttpStatus.BAD_REQUEST);
			throw new BookBadRequestException(traceId, "Please provide name to be searched as a query in the uri!!!");
		}
		
		logger.info("Trace ID : {}, Request to retrieve Book By Name as a query in uri : {}", traceId, title);
		
		// Now check if trace id is provided in request by consumer. IF not, we will generate it 
		if(!LibraryApiUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}		
		
		// Note : The exception handling has been moved out to the service class 
		List<Book> bookList = bookService.searchBook(title, traceId);
		return new ResponseEntity<>(bookList,HttpStatus.FOUND);
		

	}	
	
	
	// Create a new book 
	// Note : Wherever we use Book model we have to use @Valid annotation for the validations in that class to take effect 
	// Note : A publusher should only by added by an user admin.
/*  psst..We have changed the signature to include the authorization bearer token which will be used to check if user is an admin or not
	public ResponseEntity<?> addBook(@Valid @RequestBody Book book,
										  @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)    
							 throws BookAlreadyExistsException{

 */
	@PostMapping
	public ResponseEntity<?> addBook(@Valid @RequestBody Book book,
				  						  @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId,
				  						  @RequestHeader(value = "Authorization") String bearerToken )   // The "Authorization" Header is set in the JwtAuthenticationFilter class
				  	  		throws BookAlreadyExistsException, LibraryResourceUnauthorizedException, PublisherNotFoundException{	
		
		// Now check if trace id is provided in request by consumer. IF not, we will generate it 
		if(!LibraryApiUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}	
		
		//We need to check whether the request to create a book is coming from an admin or not 
		//Validate the Authorization Header containing bearer token for admin role
		if(! LibraryApiUtils.isUserAdmin(bearerToken)){
			logger.error(LibraryApiUtils.getUserIDFromClaim(bearerToken) + " : user does not have permission to create a book!!! ");
			throw new LibraryResourceUnauthorizedException(traceId, "User not allowed to add a book!!!");
		}
		
		
		logger.info("Trace ID : {}, Request to create Book: {}", traceId,book);
		
		// Note : The exception handling has been moved out to the service class 
		bookService.addBook(book, traceId);
		return new ResponseEntity<>(book, HttpStatus.CREATED);
	}
	
	
	//Update a Book
	@PutMapping(path = "/{bookId}")
	
	/*  -->  psst..We have changed the signature to include the authorization bearer token
	public ResponseEntity<?> UpdateBookbyId(@PathVariable Integer bookId, 
												 @ Valid @RequestBody Book bookTobeUpdated,
												 @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId) throws BookNotFoundException, BookNotUpdatedException{
	
	*/
	public ResponseEntity<?> UpdateBookbyId(@PathVariable Integer bookId,
				 								 @ Valid @RequestBody Book bookTobeUpdated,
				 								 @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId,
				 								 @RequestHeader(value = "Authorization") String bearerToken) 
				 			 throws BookNotFoundException, BookNotUpdatedException, LibraryResourceUnauthorizedException{		
		
		
		// Now check if trace id is provided in request by consumer. Ie not, we will generate it ...
		if(!LibraryApiUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}
		

		//We need to check whether the request to update a book is coming from an admin or not 
		//Validate the Authorization Header containing bearer token for admin role
		if(! LibraryApiUtils.isUserAdmin(bearerToken)){
			logger.error(LibraryApiUtils.getUserIDFromClaim(bearerToken) + " : user does not have permission to update a book!!! ");
			throw new LibraryResourceUnauthorizedException(traceId, "User not allowed to update a book!!!");
		}	

		logger.info("Trace ID : {}, Request to update Book: {}", traceId,bookTobeUpdated);
		
		// We need to set the book id with what has been supplied in the path ,else whatever book is passed in parm will be updated.
		bookTobeUpdated.setBookID(bookId);
		// Note : The exception handling has been moved out to the service class 
		bookService.updateBookbyId(bookTobeUpdated, traceId);
		return new ResponseEntity<>(bookTobeUpdated,HttpStatus.OK);
		

	}
	
	// Delete book by id 
	@DeleteMapping(path = "/{bookId}")
	public ResponseEntity<?> deleteBookbyId(@PathVariable Integer bookId,
			 									 @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId,
			 									 @RequestHeader(value = "Authorization") String bearerToken) 
			 				 throws BookNotFoundException, BookNotDeletedException, LibraryResourceUnauthorizedException{
		
		// Now check if trace id is provided in request by consumer. IF not, we will generate it 
		if(!LibraryApiUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}
		
		
		//We need to check whether the request to delete a book is coming from an admin or not 
		//Validate the Authorization Header containing bearer token for admin role
		if(! LibraryApiUtils.isUserAdmin(bearerToken)){
			logger.error(LibraryApiUtils.getUserIDFromClaim(bearerToken) + " : user does not have permission to delete a book!!! ");
			throw new LibraryResourceUnauthorizedException(traceId, "User not allowed to delete a book!!!");
		}	
	
		
		logger.info("Trace ID : {}, Request to delete Book: {}", traceId,bookId);
		// Note : The exception handling has been moved out to the service class 
		bookService.deleteBookbyId(bookId, traceId);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
		

	}
	

	//Add authors to a Book 
	@PutMapping(path = "/{bookId}/authors")
	
	public ResponseEntity<?> addBookAuthours(@PathVariable Integer bookId,
				 								 @RequestBody Set<Integer> authorIds,
				 								 @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId,
				 								 @RequestHeader(value = "Authorization") String bearerToken) 
				 			 throws BookNotFoundException, BookNotUpdatedException, LibraryResourceUnauthorizedException, BookBadRequestException, LibraryResourceNotFoundException{		
		
		
		// Now check if trace id is provided in request by consumer. Ie not, we will generate it ...
		if(!LibraryApiUtils.doesStringValueExist(traceId)){
			traceId = UUID.randomUUID().toString();
		}
		
		
		// Validate the author id in request 
		if(authorIds == null || authorIds.size() == 0 ){
			logger.error("Trace ID: {}, Author ids list  is not provided... Hope you found your special reserved slot for parking, dumbo!!! ",traceId);
			throw new BookBadRequestException(traceId, "Please supply atleast one author id in the author ids list !!!");
			
		}

		//We need to check whether the request to update a book is coming from an admin or not 
		//Validate the Authorization Header containing bearer token for admin role
		if(! LibraryApiUtils.isUserAdmin(bearerToken)){
			logger.error(LibraryApiUtils.getUserIDFromClaim(bearerToken) + " : user does not have permission to add authors to a book!!! ");
			throw new LibraryResourceUnauthorizedException(traceId, "User not allowed to to add authors to abook!!!");
		}	

		logger.info("Trace ID : {}, Request to add Authors to BookId: {}", traceId, bookId);
		
		// Now add the authors to the book
		Book bookTobeUpdatedWithAuthors = bookService.addBookAuthors(bookId, authorIds,traceId);
		
		
		// finally return the response 
		logger.debug("Returning response for trace id :{}",traceId);
		return new ResponseEntity<>(bookTobeUpdatedWithAuthors,HttpStatus.OK);
		

	}
		

}
