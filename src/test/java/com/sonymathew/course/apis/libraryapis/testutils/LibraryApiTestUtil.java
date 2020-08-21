package com.sonymathew.course.apis.libraryapis.testutils;

import com.sonymathew.course.apis.libraryapis.author.Author;
import com.sonymathew.course.apis.libraryapis.author.AuthorEntity;
import com.sonymathew.course.apis.libraryapis.model.common.Gender;
import com.sonymathew.course.apis.libraryapis.publisher.Publisher;
import com.sonymathew.course.apis.libraryapis.publisher.PublisherEntity;
import com.sonymathew.course.apis.libraryapis.user.Role;
import com.sonymathew.course.apis.libraryapis.user.User;
import com.sonymathew.course.apis.libraryapis.user.UserEntity;

import java.time.LocalDate;
import java.util.Optional;

public class LibraryApiTestUtil {
	
	public static int apiCtr;   // we need this to create different users each time to avoid duplicate user error in DB.
	
    public static Publisher createPublisher() {
        return new Publisher(null, TestConstants.TEST_PUBLISHER_NAME + apiCtr++,
                TestConstants.TEST_PUBLISHER_EMAIL,
                TestConstants.TEST_PUBLISHER_PHONE);
    }

    public static PublisherEntity createPublisherEntity() {
        return new PublisherEntity(TestConstants.TEST_PUBLISHER_NAME,
                TestConstants.TEST_PUBLISHER_EMAIL,
                TestConstants.TEST_PUBLISHER_PHONE);
    }

    public static Optional<PublisherEntity> createPublisherEntityOptional() {
        return Optional.of(createPublisherEntity());
    }

    public static Author createAuthor() {
        return new Author(null, TestConstants.TEST_AUTHOR_FIRST_NAME + apiCtr++,
                TestConstants.TEST_AUTHOR_LAST_NAME, LocalDate.now().minusYears(30), Gender.Female);
    }

    public static AuthorEntity createAuthorEntity() {
        return new AuthorEntity(TestConstants.TEST_AUTHOR_FIRST_NAME,
                TestConstants.TEST_AUTHOR_LAST_NAME, LocalDate.now().minusYears(30), Gender.Female);
    }

    public static Optional<AuthorEntity> createAuthorEntityOptional() {
        return Optional.of(createAuthorEntity());
    }

    public static User createUser(String username) {
    	
   
    	return new User(TestConstants.TEST_USER_FIRST_NAME + apiCtr++, 
    			        TestConstants.TEST_USER_PASSWORD, 
    			        TestConstants.TEST_USER_FIRST_NAME + apiCtr++, 
    			        TestConstants.TEST_USER_LAST_NAME, 
    			        LocalDate.now().minusYears(30), 
    			        Gender.Female, 
    			        TestConstants.TEST_USER_PHONE, 
    			        TestConstants.TEST_USER_FIRST_NAME + apiCtr++, 
    			        Role.USER);
//        return new User(username + apiCtr++, TestConstants.TEST_USER_FIRST_NAME,
//        		TestConstants.TEST_USER_LAST_NAME,
//                TestConstants.TEST_USER_LAST_NAME, LocalDate.now().minusYears(30), Gender.Female, TestConstants.TEST_USER_PHONE,
//                username + apiCtr + "@email.com");
    			

    }

    public static UserEntity createUserEntity(String username) {
        UserEntity be = new UserEntity(username, TestConstants.TEST_USER_PASSWORD, TestConstants.TEST_USER_FIRST_NAME,
                TestConstants.TEST_USER_LAST_NAME, LocalDate.now().minusYears(20), TestConstants.TEST_USER_GENDER,
                TestConstants.TEST_USER_PHONE, TestConstants.TEST_USER_EMAIL, "USER");
        return be;
    }

    public static Optional<UserEntity> createUserEntityOptional(String username) {
        return Optional.of(createUserEntity(username));
    }

	public static String createLoginBody(String username, String password) {
		return "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";   // This is the JSON format we use for creatign the login request body in POSTMAN. 
				
	}
}
