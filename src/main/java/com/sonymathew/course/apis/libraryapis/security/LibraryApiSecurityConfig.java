
package com.sonymathew.course.apis.libraryapis.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


// This class is used to configure our Authentication(JwtAuthenticationFilter.java) and Authorization(JwtAuthorizationFilter.java) classes to teh Spring Security Filter Chain 
@EnableWebSecurity // Allows Spring to find configs & components for security & automatically applies to Global Web Security
public class LibraryApiSecurityConfig extends WebSecurityConfigurerAdapter{
// Note : 	WebSecurityConfigurerAdapter allows us to customize Spring Security Framework
	
	// create logger
	private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	
	private UserDetailsServiceImpl userDetailsServiceImpl; // We will need to annotate the UserDetailsServiceImpl class with @Service so that it can be autowired here
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
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
	


}

