package com.sonymathew.course.apis.library.utils;

public class LibraryUtils {
	
	
	public static boolean doesStringValueExist(String stringToBeChecked) {
			if(stringToBeChecked!=null && stringToBeChecked.trim().length() > 0){
				return true;
			}else{
				return false;
			}
			
	}
	

}
