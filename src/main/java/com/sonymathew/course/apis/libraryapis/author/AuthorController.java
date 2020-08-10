package com.sonymathew.course.apis.libraryapis.author;

import com.sonymathew.course.apis.libraryapis.utils.*;
import com.sonymathew.course.apis.libraryapis.author.Author;
import com.sonymathew.course.apis.libraryapis.author.AuthorService;
import com.sonymathew.course.apis.libraryapis.exception.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(path = "/v1/authors")
public class AuthorController {

    private static Logger logger = LoggerFactory.getLogger(AuthorController.class);

    private AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping(path = "/{authorId}")
    public ResponseEntity<?> getAuthor(@PathVariable Integer authorId,
                                          @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceNotFoundException {

        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        return new ResponseEntity<>(authorService.getAuthor(authorId, traceId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addAuthor(@Valid @RequestBody Author author,
                                       @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId,
                                       @RequestHeader(value = "Authorization") String bearerToken )   // The "Authorization" Header is set in the JwtAuthenticationFilter class)
            throws LibraryResourceAlreadyExistsException, LibraryResourceUnauthorizedException {

        logger.debug("Request to add Author: {}", author);
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        logger.debug("Added TraceId: {}", traceId);
        authorService.addAuthor(author, traceId);
        
		//We need to check whether the request to create an author is coming from an admin or not 
		//Validate the Authorization Header containing bearer token for admin role
		if(! LibraryApiUtils.isUserAdmin(bearerToken)){
			logger.error(LibraryApiUtils.getUserIDFromClaim(bearerToken) + " : user does not have permission to add an author!!! ");
			throw new LibraryResourceUnauthorizedException(traceId, "User not allowed to add an author!!!");
		}       
        
        
        logger.debug("Returning response for TraceId: {}", traceId);
        return new ResponseEntity<>(author, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{authorId}")
    public ResponseEntity<?> updateAuthor(@PathVariable Integer authorId,
                                             @Valid @RequestBody Author author,
                                             @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId,
                                             @RequestHeader(value = "Authorization") String bearerToken )   // The "Authorization" Header is set in the JwtAuthenticationFilter class)
            throws LibraryResourceNotFoundException, LibraryResourceUnauthorizedException {

        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        
		//We need to check whether the request to update an author is coming from an admin or not 
		//Validate the Authorization Header containing bearer token for admin role
		if(! LibraryApiUtils.isUserAdmin(bearerToken)){
			logger.error(LibraryApiUtils.getUserIDFromClaim(bearerToken) + " : user does not have permission to update an author!!! ");
			throw new LibraryResourceUnauthorizedException(traceId, "User not allowed to update an author!!!");
		}	
       
        author.setAuthorId(authorId);
        authorService.updateAuthor(author, traceId);

        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{authorId}")
    public ResponseEntity<?> deleteAuthor(@PathVariable Integer authorId,
                                          @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId,
                                          @RequestHeader(value = "Authorization") String bearerToken) 
            throws LibraryResourceNotFoundException, LibraryResourceUnauthorizedException {

        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        
		//We need to check whether the request to delete an author is coming from an admin or not 
		//Validate the Authorization Header containing bearer token for admin role
		if(! LibraryApiUtils.isUserAdmin(bearerToken)){
			logger.error(LibraryApiUtils.getUserIDFromClaim(bearerToken) + " : user does not have permission to delete an author!!! ");
			throw new LibraryResourceUnauthorizedException(traceId, "User not allowed to delete an author!!!");
		}	       

        authorService.deleteAuthor(authorId, traceId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<?> searchAuthor(@RequestParam String firstName, @RequestParam String lastName,
                                             @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceBadRequestException {

        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        if(!LibraryApiUtils.doesStringValueExist(firstName) && !LibraryApiUtils.doesStringValueExist(lastName)) {
            logger.error("TraceId: {}, Please enter at least one search criteria to search Authors!!", traceId);
            throw new LibraryResourceBadRequestException(traceId, "Please enter a name to search Author.");
        }

        return new ResponseEntity<>(authorService.searchAuthor(firstName, lastName, traceId), HttpStatus.OK);
    }
}
