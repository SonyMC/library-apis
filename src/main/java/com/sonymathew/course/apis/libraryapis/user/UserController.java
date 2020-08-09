package com.sonymathew.course.apis.libraryapis.user;

import com.sonymathew.course.apis.libraryapis.exception.*;
import com.sonymathew.course.apis.libraryapis.user.User;
import com.sonymathew.course.apis.libraryapis.user.UserService;
import com.sonymathew.course.apis.libraryapis.utils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(path = "/v1/users")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Integer userId,
                                          @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceNotFoundException {

        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        return new ResponseEntity<>(userService.getUser(userId, traceId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addUser(@Valid @RequestBody User user,
                                          @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceAlreadyExistsException {

        logger.debug("Request to add User: {}", user);
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        logger.debug("Added TraceId: {}", traceId);
        userService.addUser(user, traceId);

        logger.debug("Returning response for TraceId: {}", traceId);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Integer userId,
                                        @Valid @RequestBody User user,
                                        @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId,
                                        @RequestHeader(value = "Authorization") String bearerToken) 
                              throws LibraryResourceNotFoundException, LibraryResourceUnauthorizedException {

        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        
		//We will not allow even an admin to update another user.
		if(LibraryApiUtils.isUserAdmin(bearerToken)){
			logger.error("Trace ID : {}, Sorry..being an admin does not give you the right to correct others!!!", traceId);
			throw new LibraryResourceUnauthorizedException(traceId, "Sorry..being an admin does not give you the right to correct others!!!");
		}
		
		// Now we will only allow a user to update own details
		int userIdInClaim = LibraryApiUtils.getUserIDFromClaim(bearerToken);
		if(userIdInClaim != userId ){ // i.e. the user is tryign to update someone else
			logger.error("Trace ID : {}, UserID :{} - : user has bad manners tryign to correct someone else!!! ",traceId,userId);
			throw new LibraryResourceUnauthorizedException(traceId, "It is bad manners trying to correct someone else!!!");
		}

        user.setUserId(userId);
        userService.updateUser(user, traceId);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId,
                                        @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId,
                                        @RequestHeader(value = "Authorization") String bearerToken) 
            throws LibraryResourceNotFoundException, LibraryResourceUnauthorizedException {

        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        
		// User can delete only his/her own details
		int userIdInClaim = LibraryApiUtils.getUserIDFromClaim(bearerToken);
		if(!LibraryApiUtils.isUserAdmin(bearerToken) && userIdInClaim != userId){ // i.e. the user not te admin and is tryign to delete someone else
			logger.error("Trace ID : {}, UserID :{} - : user is a psychopath and is trying to erase someone else!!! ",traceId,userId);
			throw new LibraryResourceUnauthorizedException(traceId, "Consult a psychiatrist ... cannot allow you to deelte someone else..though suicide is allowed!!!");
		}      

        userService.deleteUser(userId, traceId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<?> searchUser(@RequestParam String firstName, @RequestParam String lastName,
                                             @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceBadRequestException {

        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        if(!LibraryApiUtils.doesStringValueExist(firstName) && !LibraryApiUtils.doesStringValueExist(lastName)) {
            logger.error("TraceId: {}, Please enter at least one search criteria to search Users!!", traceId);
            throw new LibraryResourceBadRequestException(traceId, "Please enter a name to search User.");
        }

        return new ResponseEntity<>(userService.searchUser(firstName, lastName, traceId), HttpStatus.OK);
    }
}
