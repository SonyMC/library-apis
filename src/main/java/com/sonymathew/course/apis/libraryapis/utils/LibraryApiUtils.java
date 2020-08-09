package com.sonymathew.course.apis.libraryapis.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sonymathew.course.apis.libraryapis.security.SecurityConstants;

public class LibraryApiUtils {
	
	
	public static boolean doesStringValueExist(String stringToBeChecked) {
			if(stringToBeChecked!=null && stringToBeChecked.trim().length() > 0){
				return true;
			}else{
				return false;
			}
			
	}

	public static boolean isUserAdmin(String bearerToken) {
		
		// the same secret key used in JwtAuthenticationFilter class will be required to decrypt the token 
		Algorithm algorithmHS = Algorithm.HMAC512(SecurityConstants.SIGNING_SECRET.getBytes());
		
		String userRoleFromJwt = JWT  // decrypt the JWT token in header using the secret key 
				.require(algorithmHS)   // use the same secret key to decrypt for verfication
				.build() //JWT verifier
				.verify(bearerToken.replace(SecurityConstants.BEARER_TOKEN_PREFIX, ""))  // remove the prefix we had put in previously 
				.getClaim("role").asString();  // we had added a customized claim for role in JwtAuthenticationFilter class
	
		return userRoleFromJwt.equals("ADMIN");  // will return TRUE if user role is an admin. Else will return FALSE.
	}

	public static int getUserIDFromClaim(String bearerToken) {
		
		// the same secret key used in JwtAuthenticationFilter class will be required to decrypt the token 
		Algorithm algorithmHS = Algorithm.HMAC512(SecurityConstants.SIGNING_SECRET.getBytes());
		
		int userIdFromJwt = JWT  // decrypt the JWT token in header using the secret key 
				.require(algorithmHS)   // use the same secret key to decrypt for verfication
				.build() //JWT verifier
				.verify(bearerToken.replace(SecurityConstants.BEARER_TOKEN_PREFIX, ""))  // remove the prefix we had put in previously 
				.getClaim("userid").asInt();  // we had added a customized claim for id in JwtAuthenticationFilter class
	
		return userIdFromJwt;  // will return TRUE if user role is an admin. Else will return FALSE.
	}
	

}
