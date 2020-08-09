
package com.sonymathew.course.apis.libraryapis.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.management.RuntimeErrorException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

//1. This class will be called by the user after getting the JWT token in the JwtAuthenticationFilter class.
//2. Thsi calls will essentially allow the user to call subsequent APIs using the JWT issued above
//3. We will extend BasicAuthenticationFilter which is used to authorize details in the Http Request Header 
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

	// create logger
	private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	
	// Auto-generated constructor. 
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
		
	}
	
	
	// Get authorization header and do subsequent processing.
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		
		// get the header which we had set while generating the JWT token earlier in JwtAuthenticationFilter class.
		String authorizationHeader = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
		

		// In case there is no authorization header or there is one but it does not contain the JWT we had provided
		// then ignore header & return control back to filter back to filter chain 
		if(authorizationHeader == null	|| !authorizationHeader.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)){
			chain.doFilter(request, response);
			return;
		}
		
		// if everything is ok,then :
		 //1. then fetch the authentication details from the header 
		// 2. verify the JWT token 
		// 3. set the security context using the authentication token
		UsernamePasswordAuthenticationToken authenticationToken= getAuthentication(authorizationHeader);
						
		// Now set the security context
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		
		// finally continue with the filter chain 
		chain.doFilter(request, response);
		
	}


	private UsernamePasswordAuthenticationToken getAuthentication(String authorizationHeader) {
		
		// the same secret key used in JwtAuthenticationFilter class will be required to decrypt the token 
		Algorithm algorithmHS = Algorithm.HMAC512(SecurityConstants.SIGNING_SECRET.getBytes());
		

		if(authorizationHeader != null){
			
			try{
			String userNameFromJwt = JWT  // decrypt the JWT token in header using the secret key 
									.require(algorithmHS)   // use the same secret key to decrypt for verfication
									.build() //JWT verifier
									.verify(authorizationHeader.replace(SecurityConstants.BEARER_TOKEN_PREFIX, ""))  // remove the prefix we had put in previously 
									.getSubject(); // we had set the user name as the subject previously 
			
			// now return the authentication token if our decryption above worked.
			if(userNameFromJwt != null){
				return new UsernamePasswordAuthenticationToken(userNameFromJwt, null, new ArrayList<>());
				
				}
					
			}catch(BadCredentialsException bex){
				logger.error("Authurization Failed!!",bex.getMessage());
				throw bex;
			}catch(Error ex){
				logger.error("Error while authorizing!!!",ex.getMessage());
				throw new RuntimeErrorException(ex);
			}

		}
		return null;
		
	}
	
	
	

}
