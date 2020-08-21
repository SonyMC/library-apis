package com.sonymathew.course.apis.libraryapis.testutils;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.sonymathew.course.apis.libraryapis.user.User;


@Component
public class LibraryApiIntegrationTestUtil {
	
	@Autowired
	TestRestTemplate testRestTemplate;
	
    @Value("${library.api.user.admin.username}")
    private String adminUsername;

    @Value("${library.api.user.admin.password}")
    private String adminPassword;
    
    @Autowired
    Environment environment;	
    
    //Will be used fro admin login
	private ResponseEntity<String> adminLoginResponse;	
	

	
	public ResponseEntity<User> registerNewUser(String username){
		
		//The port on which the server started.
		String port = environment.getProperty("local.server.port");
		
		// Register user uri 
		URI registerUri = null;
		
		try{
		   registerUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_REGISTER_URL);
			System.out.println("registerUri********* " + registerUri);
		} catch(URISyntaxException usex){
			usex.printStackTrace();
		}
		
		// Now create HTTP user create request using the utility method to create a new user
		HttpEntity<User> newUserRequest = new HttpEntity<>(LibraryApiTestUtil.createUser(username));
		
		// ResponseEntity<User> responseEntity = new  ResponseEntity<>(HttpStatus.CONTINUE);
		
		// Now call the registerd uri ( i.e. POST for /v1/users) usign the testRestTemplate
		return testRestTemplate.postForEntity(registerUri, newUserRequest, User.class);
		 //responseEntity = testRestTemplate.postForEntity(registerUri, newUserRequest, User.class);
		 //System.out.println("ResponseEntity********* " + responseEntity.toString());
		 //return responseEntity;
		
	}
	
	
	public ResponseEntity<String> loginUser(String username,String password){
		
		//The port on which the server started.
		String port = environment.getProperty("local.server.port");
		
		// We will have to login as admin multiple times 
		// Once the admin has ogged in, do not keep creating requests for login.The class variable adminLoginResponse is populated below only if there is no value in it.
		if(username.equals("adminUsername") && (adminLoginResponse != null)){
			return adminLoginResponse;
		}
		
		// Login uri
		URI loginUri = null;
		
		try{
			loginUri = new URI(TestConstants.API_BASE_URL + port +TestConstants.LOGIN_URL);
			System.out.println("loginUri********* " + loginUri);
		} catch(URISyntaxException usex){
			usex.printStackTrace();
		}
		// Now create HTTP user create request using the utility method to create a new user
		HttpEntity<String> loginRequest = new HttpEntity<>(LibraryApiTestUtil.createLoginBody(username,password));
		
		
		// Now call the registerd uri ( i.e. POST for /login) usign the testRestTemplate
		ResponseEntity<String> responseEntity=	 testRestTemplate.postForEntity(loginUri, loginRequest, String.class);		
		
		// If the user is an admin, set the class variable we had defined before to check against repeated logins
		if(username.equals("adminUsername")){
			adminLoginResponse = responseEntity;
		}
		
		return responseEntity; 
		
	}

   // Note : Authorization header can be defined as a multimap usign a key,value pair where the key will be soen strinfg ( here "Authorization") and the value will be our BearerToken
	public MultiValueMap<String, String> createAuthorizationHeader(String bearerToken) {
		
		// Define a multi value map -> allows duplicate values for keys
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		
		headers.add("Authorization", bearerToken);
		
		return headers;
		
		
		
		
	}

}
