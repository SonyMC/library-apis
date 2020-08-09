package com.sonymathew.course.apis.libraryapis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class LibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
	}
	
    // We want to encrypt the pwd ' SecurityConstants.NEW_USER_DEFAULT_PASSWORD' in UserService.class by hashing it rather than storing it as plain text
   @Bean
   public BCryptPasswordEncoder bCryptPasswordEncoder(){
	   return new BCryptPasswordEncoder();
   }	

}
