package com.sonymathew.course.apis.libraryapis;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.sonymathew.course.apis.libraryapis.model.common.Gender;
import com.sonymathew.course.apis.libraryapis.user.UserEntity;
import com.sonymathew.course.apis.libraryapis.user.UserRepository;

// This class will be used to create admin user

@Component //means that Spring framework will autodetect these classes for dependency injection when annotation-based configuration and classpath scanning is used
public class ApplicationInitializer {

	BCryptPasswordEncoder bCryptPasswordEncoder;
	UserRepository userRepository;


	//Constructor based dependency injection.
	public ApplicationInitializer(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userRepository = userRepository;
	}
	
	
	@Value("${library.api.user.admin.username:lib-admin}")  //taken from Application.properties; If value is not found use default provided viz. "lib-admin".
	String adminUserName;
	
	@Value("${library.api.user.admin.password:lib-pwd}")  //taken from Application.properties; If value is not found use default provided viz. "lib-pwd".
	String adminPassword;
	
	// Use init method to insert admin record inot database
	@PostConstruct // We need the admin to be loaded as the application is starting
	private void init(){
		
		//Since this method will be called on each startup, it wll try to create the admin each time .
		// After first time the method will fail due to teh uniqueness contraint set in the DB.
		//So now we will need r check whther an admin already exists in DB and continue with the method only if admin is not there.
		
		UserEntity admin = userRepository.findByUsername(adminUserName);
		
		if(admin!=null && admin.getRole().equals("ADMIN")){
			return;    // admin found so we do not need proceeed with creation;
		}else
		{
		// create admin entity 
		 admin = new UserEntity(
								adminUserName,
								bCryptPasswordEncoder.encode(adminPassword), 
								"Library", 
								"Admin", 
								LocalDate.now().minusYears(30),  // let the age be 30 years and DOB wll be set accordingly
								Gender.Female, 
								"999-888-777", 
								"library.admin@library.com", 
								"ADMIN");
		}
		// Now add the admin to the DB
		userRepository.save(admin);
		
	}
	
}
