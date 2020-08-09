package com.sonymathew.course.apis.libraryapis.utils;

public class LibraryApiUtils {
	
	
	public static boolean doesStringValueExist(String stringToBeChecked) {
			if(stringToBeChecked!=null && stringToBeChecked.trim().length() > 0){
				return true;
			}else{
				return false;
			}
			
	}
	

}
