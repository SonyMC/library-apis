package com.sonymathew.course.apis.libraryapis.user;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.sonymathew.course.apis.libraryapis.testutils.TestConstants;

/*
 * Remember... in integration testing we will be testign the API which is exposed to the outer world. Hence we test the controller class
 * 
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)  // this will start the server on a random port 
public class UserControllerTest {

	@Autowired  // Spring takes care of instantiation and Dependency Injection
	TestRestTemplate testRestTemplate;
	
	@Autowired
	com.sonymathew.course.apis.libraryapis.testutils.LibraryApiIntegrationTestUtil libraryApiIntegrationTestUtil ; 
	
    @Autowired
    Environment environment;	
    
    

	
    @Before
    public void setUp() throws Exception {
        
    }
    
    @Test
    public void registerUser_success() {
    	
    	// We are expecting an User object in the response...
    	ResponseEntity<User> response = libraryApiIntegrationTestUtil.registerNewUser("register.user.success");
    	
    	// Check success
    	Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());  // check success response 
    	
    	// get the user from the response and assert it is valid
    	User responseLibUser = response.getBody();
    	Assert.assertNotNull(responseLibUser);
    	Assert.assertTrue(responseLibUser.getUsername().contains("register.user.success"));   //each time user name will change dynamically because we are appending a counter. So we can only test for cotnains , not equal.
    	Assert.assertNotNull(responseLibUser.getUserId());
    	Assert.assertNotNull(responseLibUser.getPassword());
    	Assert.assertNotNull(responseLibUser.getRole());
    	
 	
    }

    
    @Test
    public void getUser_success() {
    	
        //The port on which the server started.
        String port = environment.getProperty("local.server.port");
        
       	// First we will register the user bvefore we login ..
    	ResponseEntity<User> response = libraryApiIntegrationTestUtil.registerNewUser("get.user.success");
    	
    	// Check success
    	Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());  // check success response 
    	
    	// get the user from the response and assert it is valid
    	User user = response.getBody();
    	
    	// Asser user is not null 
    	Assert.assertNotNull(user);
    	
    	// Get the user id 
    	Integer userId = user.getUserId();
    	
    	// Login with the credentials 
    	ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(user.getUsername(), user.getPassword());
    	
    	
    	Assert.assertEquals(HttpStatus.CREATED, loginResponse.getStatusCode());  // check success response )
    	
    	// Now get the user 
		
    	// Register user uri ->  v1/users/{userId}
		URI getUserUri = null;  
		
		try{
			getUserUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL +  "/" + userId);
			} catch(URISyntaxException usex){
			usex.printStackTrace();
		}
		
		// Login response will have the  JWT  token  which  we  need to fetch  and pass 
		// The header can be defined as a multimap having a key,value pair
		// *** Note : The following method can be used to extract an authorization header from a response entity...this will be useful as this is what we will need practially to exchange info with an outside TP app!!!
		String authHeader = loginResponse.getHeaders().get("Authorization").get(0); //The get(0) means get first Header named  Authorization -> refer /library-api-v2/src/main/java/com/sonymathew/course/apis/libraryapis/security/JwtAuthorizationFilter.java
		
		
		//Now create a new authorizaton header which will be used to sedn the user requests 
		MultiValueMap<String, String> authorizationHeaderMultiMap = libraryApiIntegrationTestUtil.createAuthorizationHeader(authHeader);  // create Header from login response. 
				
		
		// Now call the registerd uri ( i.e. POST for /v1/users) usign the testRestTemplate
		// To send header we neded to use the exchange method 
		ResponseEntity<User> libUserResponse = 
		testRestTemplate.exchange(getUserUri,
				                  HttpMethod.GET,
				                  new HttpEntity<Object>(authorizationHeaderMultiMap),
				                  User.class
				                  );
				                         

		
        Assert.assertEquals(HttpStatus.OK, libUserResponse.getStatusCode());
        User libUser = libUserResponse.getBody();

        Assert.assertNotNull(libUser);
        Assert.assertNotNull(libUser.getUserId());
        Assert.assertTrue(libUser.getUsername().contains("get.user.success"));
        Assert.assertNotNull(libUser.getRole());		
    }


    
    @Test
    public void getUser_user_does_not_exist() {
        //The port on which the server started.
        String port = environment.getProperty("local.server.port");
    		
    	
    	// First we will register the user bvefore we login ..
    	ResponseEntity<User> response = libraryApiIntegrationTestUtil.registerNewUser("get.user.does.not.exist");
    	
    	// Check success
    	Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());  // check success response 
    	
    	// get the user from the response and assert it is valid
    	User user = response.getBody();
    	
    	// Asser user is not null 
    	Assert.assertNotNull(user);
    	
    	
    	// Login with the credentials 
    	ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(user.getUsername(), user.getPassword());
    	
    	
    	Assert.assertEquals(HttpStatus.CREATED, loginResponse.getStatusCode());  // check success response )
    	
    	// Now get the user 
		
    	// Register user uri ->  v1/users/{userId}
		URI getUserUri = null;  
		
		try{ 
			getUserUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL +  "/" + 1234 ); //obviously no user exists as 1234 !!!
			} catch(URISyntaxException usex){
			usex.printStackTrace();
		}
		
		// Login response will have the  JWT  token  which  we  need to fetch  and pass 
		// The header can be defined as a multimap having a key,value pair
		// *** Note : The following method can be used to extract an authorization header from a response entity...this will be useful as this is what we will need practially to exchange info with an outside TP app!!!
		String authHeader = loginResponse.getHeaders().get("Authorization").get(0); //The get(0) means get first Header named  Authorization -> refer /library-api-v2/src/main/java/com/sonymathew/course/apis/libraryapis/security/JwtAuthorizationFilter.java
		
		
		//Now create a new authorizaton header which will be used to sedn the user requests 
		MultiValueMap<String, String> authorizationHeaderMultiMap = libraryApiIntegrationTestUtil.createAuthorizationHeader(authHeader);  // create Header from login response. 
				
		
		// Now call the registerd uri ( i.e. POST for /v1/users) usign the testRestTemplate
		// To send header we neded to use the exchange method 
		ResponseEntity<User> libUserResponse = 
		testRestTemplate.exchange(getUserUri,
				                  HttpMethod.GET,
				                  new HttpEntity<Object>(authorizationHeaderMultiMap),
				                  User.class
				                  );
				                         

		
        Assert.assertEquals(HttpStatus.FORBIDDEN, libUserResponse.getStatusCode());

    }
    
    

    @Test
    public void getUser_unauthorized_for_different_user() {

         String port = environment.getProperty("local.server.port");

        // First we register a user
        ResponseEntity<User> response = libraryApiIntegrationTestUtil.registerNewUser("getUser_unauthorized_for_different.user");
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        User responseUser = response.getBody();
        Integer userId = responseUser.getUserId() + 1;

        // Login with the credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(responseUser.getUsername(), responseUser.getPassword());
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        // Now we get the registered user
        URI getUserUri = null;
        try {
            getUserUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" + userId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> headers = libraryApiIntegrationTestUtil.createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        ResponseEntity<String> libUserResponse = testRestTemplate.exchange(
                getUserUri, HttpMethod.GET, new HttpEntity<Object>(headers),
                String.class);

        // Unauthorized to get another user details
        Assert.assertEquals(HttpStatus.FORBIDDEN, libUserResponse.getStatusCode());
    }
    


    @Test
    public void updateUser_success() {

        String port = environment.getProperty("local.server.port");

        // First we register a user
        ResponseEntity<User> response = libraryApiIntegrationTestUtil.registerNewUser("update.user.success");

        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        User responseUser = response.getBody();
        Assert.assertNotNull(responseUser);
        Integer userId = responseUser.getUserId();

        // Login with the credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(responseUser.getUsername(), responseUser.getPassword());

        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        // Set the values to be updated
        responseUser.setPassword("NewPassword");
        responseUser.setEmailId("newemailaddress@email.com");
        responseUser.setPhoneNumber(TestConstants.TEST_USER_PHONE_UPDATED);

        URI userUri = null;
        try {
            userUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" + userId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> headers = libraryApiIntegrationTestUtil.createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        HttpEntity<User> request = new HttpEntity<>(responseUser, headers);
        ResponseEntity<User> libUserResponse = testRestTemplate.exchange(
                userUri, HttpMethod.PUT, request,
                User.class);

        Assert.assertEquals(HttpStatus.OK, libUserResponse.getStatusCode());

        // Now login with changed password
        loginResponse = libraryApiIntegrationTestUtil.loginUser(responseUser.getUsername(), responseUser.getPassword());
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        // Now we get the user. Should have update email and phone number
        // Put the new Auth token
        headers.replace("Authorization", loginResponse.getHeaders().get("Authorization"));

        libUserResponse = testRestTemplate.exchange(
                userUri, HttpMethod.GET, new HttpEntity<Object>(headers),
                User.class);

        Assert.assertEquals(HttpStatus.OK, libUserResponse.getStatusCode());
        User libraryUser = libUserResponse.getBody();
        Assert.assertEquals("newemailaddress@email.com", libraryUser.getEmailId());
        Assert.assertEquals(TestConstants.TEST_USER_PHONE_UPDATED, libraryUser.getPhoneNumber());
    }

    @Test
    public void updateUser_unauthorized_for_different_user() {

        String port = environment.getProperty("local.server.port");

        // First we register a user
        ResponseEntity<User> response = libraryApiIntegrationTestUtil.registerNewUser("update.user.unauthorized.for.different.user");
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        User responseUser = response.getBody();
        Assert.assertNotNull(responseUser);
        // Update the details of another userId (userId + 1)
        Integer userId = responseUser.getUserId() + 1;

        // Login with the credentials to perform update
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(responseUser.getUsername(), responseUser.getPassword());
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        // Set the values to be updated
        responseUser.setPassword("NewPassword");
        responseUser.setEmailId("newemailaddress@email.com");
        responseUser.setPhoneNumber(TestConstants.TEST_USER_PHONE_UPDATED);

        URI userUri = null;
        try {
            userUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" + userId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> headers = libraryApiIntegrationTestUtil.createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        HttpEntity<User> request = new HttpEntity<>(responseUser, headers);

        // We need to use RestTemplate because we need to set the ErrorHandler becaue TestRestTemplate
        // throws HttpRetryException when there is a response body with response status 401
        RestTemplate rTemplate = new RestTemplate();
        rTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        rTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            public boolean hasError(ClientHttpResponse response) throws IOException {
                HttpStatus statusCode = response.getStatusCode();
                return statusCode.series() == HttpStatus.Series.SERVER_ERROR;
            }
        });

        ResponseEntity<String> libUserResponse = rTemplate.exchange(
                userUri, HttpMethod.PUT, request,
                String.class);

        // Update for another user ID should not be allowed
        Assert.assertEquals(HttpStatus.FORBIDDEN, libUserResponse.getStatusCode());
    }

    @Test
    public void deleteUser_success() {

        String port = environment.getProperty("local.server.port");

        // First we register a user
        ResponseEntity<User> response = libraryApiIntegrationTestUtil.registerNewUser("delete.user.success");
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        User responseUser = response.getBody();
        Assert.assertNotNull(responseUser);
        Integer userId = responseUser.getUserId();

        // Login with the credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(responseUser.getUsername(), responseUser.getPassword());
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        // Delete the user
        URI userUri = null;
        try {
            userUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" + userId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> headers = libraryApiIntegrationTestUtil.createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        HttpEntity<User> request = new HttpEntity<>(responseUser, headers);
        ResponseEntity<String> libUserResponse = testRestTemplate.exchange(
                userUri, HttpMethod.DELETE, request, String.class);

        Assert.assertEquals(HttpStatus.ACCEPTED, libUserResponse.getStatusCode());

        // Now login again. You should not be able to login
        loginResponse = libraryApiIntegrationTestUtil.loginUser(responseUser.getUsername(), responseUser.getPassword());
        Assert.assertEquals(HttpStatus.FORBIDDEN, loginResponse.getStatusCode());

    }

    @Test
    public void deleteUser_unauthorized_for_different_user() {

        String port = environment.getProperty("local.server.port");

        // First we register a user
        ResponseEntity<User> response = libraryApiIntegrationTestUtil.registerNewUser("delete.user.unauthorized.for.different.user");
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        User responseUser = response.getBody();
        Assert.assertNotNull(responseUser);
        // Change the userId to something else
        Integer userId = responseUser.getUserId() + 1;

        // Login with the credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(responseUser.getUsername(), responseUser.getPassword());
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        // Delete the user
        URI userUri = null;
        try {
            userUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" + userId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> headers = libraryApiIntegrationTestUtil.createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        HttpEntity<User> request = new HttpEntity<>(responseUser, headers);
        ResponseEntity<String> libUserResponse = testRestTemplate.exchange(
                userUri, HttpMethod.DELETE, request, String.class);

        Assert.assertEquals(HttpStatus.FORBIDDEN, libUserResponse.getStatusCode());

    }

    @Test
    public void searchUsers_success() {
		
		String port = environment.getProperty("local.server.port");
        // Register 10 users
        for(int i=0; i<10; i++) {
            libraryApiIntegrationTestUtil.registerNewUser("searchUsers.success" + i);
        }

        URI searchUri = null;
        try {
            searchUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_SEARCH_URI + "?firstName=" + TestConstants.TEST_USER_FIRST_NAME
                    + "&lastName=" + TestConstants.TEST_USER_LAST_NAME);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<User[]> response = testRestTemplate.getForEntity(searchUri, User[].class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        // Comparing with >= 10 because depending upon which other test methods run the number of users may vary
        Assert.assertTrue(response.getBody().length >= 10);
        for(User libraryUser : response.getBody()) {
            Assert.assertTrue(libraryUser.getFirstName().contains(TestConstants.TEST_USER_FIRST_NAME));
            Assert.assertTrue(libraryUser.getLastName().contains(TestConstants.TEST_USER_LAST_NAME));
        }
    }   
    

 
}