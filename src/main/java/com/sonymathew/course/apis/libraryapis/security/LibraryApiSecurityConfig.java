
package com.sonymathew.course.apis.libraryapis.security;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.sonymathew.course.apis.libraryapis.testutils.TestConstants;


// This class is used to configure our Authentication(JwtAuthenticationFilter.java) and Authorization(JwtAuthorizationFilter.java) classes to teh Spring Security Filter Chain 
@EnableWebSecurity // Allows Spring to find configs & components for security & automatically applies to Global Web Security
public class LibraryApiSecurityConfig extends WebSecurityConfigurerAdapter{
// Note : 	WebSecurityConfigurerAdapter allows us to customize Spring Security Framework
	
	// create logger
	private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	
	private UserDetailsServiceImpl userDetailsServiceImpl; // We will need to annotate the UserDetailsServiceImpl class with @Service so that it can be autowired here
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
    
    @Autowired
    Environment environment;
	
	// Construtor based dependency injection
	public LibraryApiSecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl,
			BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userDetailsServiceImpl = userDetailsServiceImpl;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	
	/// This function will be used to configure which requests shoud not challenged , which should be challenged and the authentication and authization filters appplied. Will also specify the session configuration.
	public void configure(HttpSecurity httpSecurity) throws Exception   {
		try {
			httpSecurity.cors().and().csrf().disable()  // disable cors(Cross Origin Resource Sharing) and csrf(Cross site resource forgery) is disabled for non browser clients
						.authorizeRequests()  // now authorize the following request matching th below criteria
				//		.antMatchers(HttpMethod.GET).permitAll()  // allow all gets 
				//		.antMatchers(HttpMethod.GET,SecurityConstants.GET_PUBLISHER_BY_ID).permitAll()//  allow get publishers by ID request 
				//		.antMatchers(HttpMethod.GET,SecurityConstants.GET_PUBLISHER_BY_NAME).permitAll()//  allow get publishers by Name request 
				//		.antMatchers(HttpMethod.GET,SecurityConstants.SEARCH_PUBLISHER_BY_NAME).permitAll()//  allow get publishers by Name Query search request 
						.antMatchers(HttpMethod.POST,SecurityConstants.NEW_USER_REGISTRATION_URL).permitAll()// allow new user creation as we do not want to challenge the first time user with a Jwt validation 
						.antMatchers(HttpMethod.POST,SecurityConstants.TEST_NEW_USER_REGISTRATION_URL).permitAll()// allow  test new user creation. For some reason for integraton testing, teh above uri is not working!!!
						.anyRequest().authenticated()   // apart from new user request all other requests will be authenticated
						.and()  // add our customized authentication & authorization fiters 
						.addFilter(new JwtAuthenticationFilter(authenticationManager())) // Authentication filter class
						.addFilter(new JwtAuthorizationFilter(authenticationManager()))  // Authorization filter class
						.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);// Since we are using Rest services, we will not need a session. By declaring the policy as Stateless, Spring Security will never create an HttpSession and it will never use it to obtain the SecurityContext
		} catch (Exception ex) {
			logger.error("Failed to configure security in class {}!!!", LibraryApiSecurityConfig.class,ex.getMessage());
			throw ex;
		} 
			
		
	}
	
	
	// Will be used to specify the User Details retrieval implmentation and pwd encoder.
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(bCryptPasswordEncoder); // let the authentication manager know what is the user details service implmentation used to get teh user details and also teh type of pwd encoder used. 

		}
	
	
	/*
	 * This method is added only to demonstrte how to configure CORS in a browser based scenario. In our Library API - THIS CAN BE WHOLLY IGNORED OR LEFT OUT!!!!
	 */
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		
        //The port on which the server started.
        String port = environment.getProperty("local.server.port");
        
        String allowedUriGeneric = TestConstants.API_BASE_URL + port + "/v1/*" ;
        String allowedUriLogin = TestConstants.API_BASE_URL + port + "/login" ;
		
        // Define an immutable list as we dont want this to keep changing 
        final List<String> allowedUris = new ArrayList<>(java.util.Arrays.asList(allowedUriGeneric,allowedUriLogin));
		
        //Define an immutable list of methods
        final List<String> allowedMethods = new ArrayList<>(java.util.Arrays.asList("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH"));
        
       //Define an immutable list of headers
        final List<String> allowedHeaders = new ArrayList<>(java.util.Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        
        // Configure CORS  
		final CorsConfiguration corsConfiguration = new CorsConfiguration();
		
		//set allowed uris 
		corsConfiguration.setAllowedOrigins(allowedUris);
		
		
		//set allowed methods
		corsConfiguration.setAllowedHeaders(allowedMethods);
		
		// setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        corsConfiguration.setAllowCredentials(true);		
        
        
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        corsConfiguration.setAllowedHeaders(allowedHeaders);
        
        // define a uri based CORS configuration source 
        final UrlBasedCorsConfigurationSource urlCorsSource = new UrlBasedCorsConfigurationSource();
        
        // register the uri based CORS configuration source using the corsConfiguration details
         urlCorsSource.registerCorsConfiguration("/**", corsConfiguration);
         
        return urlCorsSource;
        
		
	}
	
	


}

