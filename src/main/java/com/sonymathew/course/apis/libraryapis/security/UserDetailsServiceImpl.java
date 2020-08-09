
package com.sonymathew.course.apis.libraryapis.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sonymathew.course.apis.libraryapis.exception.LibraryResourceNotFoundException;
import com.sonymathew.course.apis.libraryapis.user.User;
import com.sonymathew.course.apis.libraryapis.user.UserService;



/*
/* Will load user details from DB  
*/

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	// We use USerService class for user functions
	private UserService userService;
	
	
	// Consructor based dependency injection
	public UserDetailsServiceImpl(UserService userService) {
		this.userService = userService;
	}


    // Security service which will load user from DB using name 
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		
		User user = null;
		
		try{
			user = userService.getUserByUserName(userName)	;
			}catch(LibraryResourceNotFoundException libNfEx){
			throw new UsernameNotFoundException(libNfEx.getMessage());
			}
				
			// User implements UserDetails so we can return type user 
			return user;

		}
	
}	
 