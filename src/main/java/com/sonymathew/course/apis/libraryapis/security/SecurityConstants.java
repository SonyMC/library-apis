package com.sonymathew.course.apis.libraryapis.security;

public class SecurityConstants {

    public static final long EXPIRATION_TIME = 1800000;// 30 mins
	public static final String SIGNING_SECRET = "Dollops";
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER_TOKEN_PREFIX = "Bearer ";
	public static final String NEW_USER_REGISTRATION_URL = "/v1/users/";  // This is the url for creating a user as can be seen from UserController.java
	public static final String TEST_NEW_USER_REGISTRATION_URL = "/v1/users";  // This is for testing. For soem reason teh above uroi does not work durign integration testing.
	public static final String GET_PUBLISHER_BY_ID = "/v1/publishers/*";   // This url gers publishers by id 
	public static final String GET_PUBLISHER_BY_NAME = "/v1/publishers/name/*";  // This url gets publishers by name 
	public static final String SEARCH_PUBLISHER_BY_NAME = "/v1/publishers/search?*";  // This url gets publishers by name 
	public static final String NEW_USER_DEFAULT_PASSWORD = "Password123";
    // Base URL
    public static final String API_BASE_URL = "http://localhost:";	
}
