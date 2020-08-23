package com.sonymathew.course.apis.libraryapis.user;

import com.sonymathew.course.apis.libraryapis.book.BookEntity;
import com.sonymathew.course.apis.libraryapis.book.BookRepository;
import com.sonymathew.course.apis.libraryapis.book.BookStatusEntity;
import com.sonymathew.course.apis.libraryapis.book.BookStatusRepository;
import com.sonymathew.course.apis.libraryapis.exception.*;
import com.sonymathew.course.apis.libraryapis.security.SecurityConstants;
import com.sonymathew.course.apis.libraryapis.utils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

@Service
public class UserService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private BookRepository bookRepository;
    private BookStatusRepository bookStatusRepository;
    private UserBookEntityRepository userBookEntityRepository;   
    
    @Value("${library.rule.user.book.max.times.issue: 3}")
    private int maxNumberOfTimesIssue;   
  
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		super();
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}




    public void addUser(User userToBeAdded, String traceId)
            throws LibraryResourceAlreadyExistsException {

        logger.debug("TraceId: {}, Request to add User: {}", traceId, userToBeAdded);
        UserEntity userEntity = new UserEntity(
                userToBeAdded.getUsername(),
                //SecurityConstants.NEW_USER_DEFAULT_PASSWORD,
              //bCryptPasswordEncoder.encode(SecurityConstants.NEW_USER_DEFAULT_PASSWORD),
                bCryptPasswordEncoder.encode(userToBeAdded.getPassword()) ,         
                userToBeAdded.getFirstName(),
                userToBeAdded.getLastName(),
                userToBeAdded.getDateOfBirth(),
                userToBeAdded.getGender(),
                userToBeAdded.getPhoneNumber(),
                userToBeAdded.getEmailId(),
                "USER"); // by default all users have been assigned teh role of "USER"

        userToBeAdded.setPassword(SecurityConstants.NEW_USER_DEFAULT_PASSWORD);
        UserEntity addedUser = null;

        try {
            addedUser = userRepository.save(userEntity);
        } catch (DataIntegrityViolationException e) {
            logger.error("TraceId: {}, User already exists!!", traceId, e);
            throw new LibraryResourceAlreadyExistsException(traceId, "User already exists!!");
        }

        userToBeAdded.setUserId(addedUser.getUserId());
        userToBeAdded.setRole(Role.USER);
        logger.info("TraceId: {}, User added: {}", traceId, userToBeAdded);
    }

    public User getUser(Integer userId, String traceId) throws LibraryResourceNotFoundException {

        Optional<UserEntity> userEntity = userRepository.findById(userId);
        User user = null;

        if(userEntity.isPresent()) {
            UserEntity pe = userEntity.get();
            user = createUserFromEntity(pe);
        } else {
            throw new LibraryResourceNotFoundException(traceId, "User Id: " + userId + " Not Found");
        }

        return user;
    }

    public void updateUser(User userToBeUpdated, String traceId) throws LibraryResourceNotFoundException {

        Optional<UserEntity> userEntity = userRepository.findById(userToBeUpdated.getUserId());

        if(userEntity.isPresent()) {
            UserEntity ue = userEntity.get();
            if(LibraryApiUtils.doesStringValueExist(userToBeUpdated.getEmailId())) {
                ue.setEmailId(userToBeUpdated.getEmailId());
            }
            if(LibraryApiUtils.doesStringValueExist(userToBeUpdated.getPhoneNumber())) {
                ue.setPhoneNumber(userToBeUpdated.getPhoneNumber());
            }
            if(LibraryApiUtils.doesStringValueExist(userToBeUpdated.getPassword())) {
                ue.setPassword(userToBeUpdated.getPassword());
            }
            userRepository.save(ue);
            userToBeUpdated = createUserFromEntity(ue);
        } else {
            throw new LibraryResourceNotFoundException(traceId, "User Id: " + userToBeUpdated.getUserId() + " Not Found");
        }

    }


    public void deleteUser(Integer userId, String traceId) throws LibraryResourceNotFoundException {
        try {
            userRepository.deleteById(userId);
        } catch(EmptyResultDataAccessException e) {
            logger.error("TraceId: {}, User Id: {} Not Found", traceId, userId, e);
            throw new LibraryResourceNotFoundException(traceId, "User Id: " + userId + " Not Found");
        }
    }

    public List<User> searchUser(String firstName, String lastName, String traceId) {

        List<UserEntity> userEntities = null;
        if(LibraryApiUtils.doesStringValueExist(firstName) && LibraryApiUtils.doesStringValueExist(lastName)) {
            userEntities = userRepository.findByFirstNameAndLastNameContaining(firstName, lastName);
        } else if(LibraryApiUtils.doesStringValueExist(firstName) && !LibraryApiUtils.doesStringValueExist(lastName)) {
            userEntities = userRepository.findByFirstNameContaining(firstName);
        } else if(!LibraryApiUtils.doesStringValueExist(firstName) && LibraryApiUtils.doesStringValueExist(lastName)) {
            userEntities = userRepository.findByLastNameContaining(lastName);
        }
        if(userEntities != null && userEntities.size() > 0) {
            return createUsersForSearchResponse(userEntities);
        } else {
            return Collections.emptyList();
        }
    }
    

 // This method is used in the Security layer by UserDetailsServiceImpl class
 	public User getUserByUserName(String userName) throws LibraryResourceNotFoundException {
 		UserEntity userEntity = userRepository.findByUsername(userName);
 		
 		if(userEntity!=null)
 		{
 			return(createUserFromEntityForLogin(userEntity));
 		}else{
 			throw new LibraryResourceNotFoundException("UserService.getUserByName", "Library " + userName + " not Found!!!");
 		}

 	} 
 	
    // Create user from login username and pwd 
    private User createUserFromEntityForLogin(UserEntity ue) {
        return new User(ue.getUserId(), ue.getUsername(),ue.getPassword(), ue.getFirstName(), ue.getLastName(),
                ue.getDateOfBirth(), ue.getGender(), ue.getPhoneNumber(), ue.getEmailId(), Role.valueOf(ue.getRole()));
    }
		
    
    private User createUserFromEntity(UserEntity ue) {
        return new User(ue.getUserId(), ue.getUsername(), ue.getFirstName(), ue.getLastName(),
                ue.getDateOfBirth(), ue.getGender(), ue.getPhoneNumber(), ue.getEmailId(), Role.valueOf(ue.getRole()));
    }
    


    private List<User> createUsersForSearchResponse(List<UserEntity> userEntities) {
        return userEntities.stream()
                .map(ue -> new User(ue.getUsername(), ue.getFirstName(), ue.getLastName()))
                .collect(Collectors.toList());
    }
    


    

    @Transactional
    public IssueBookResponse issueBooks(int userId, Set<Integer> bookIds, String traceId) throws LibraryResourceNotFoundException {

        Optional<UserEntity> userEntity = userRepository.findById(userId);

        if(userEntity.isPresent()) {
            Set<IssueBookStatus> issueBookStatuses = new HashSet<>(bookIds.size());
            // Find out if the supplied list of books is issue-able or not
            bookIds.stream()
                    .forEach(bookId -> {
                        Optional<BookEntity> be = bookRepository.findById(bookId);
                        IssueBookStatus bookStatus;
                        if (!be.isPresent()) {
                            bookStatus = new IssueBookStatus(bookId, "Not Issued", "Book Not Found");
                        } else {
                            BookStatusEntity bse = be.get().getBookStatus();
                            if ((bse.getTotalNumberOfCopies() - bse.getNumberOfCopiesIssued()) == 0) {
                                bookStatus = new IssueBookStatus(bookId,"Not Issued", "No copies available");
                            } else {
                                // Check if the book has already been issued to the user, and this can be re-issued
                                List<UserBookEntity> byUserIdAndBookId = userBookEntityRepository.findByUserIdAndBookId(userId, bookId);
                                if(byUserIdAndBookId != null && byUserIdAndBookId.size() > 0) {
                                    // Book can be re-issued
                                    UserBookEntity userBookEntity = byUserIdAndBookId.get(0);
                                    if(userBookEntity.getNumberOfTimesIssued() < maxNumberOfTimesIssue) {
                                        userBookEntity.setNumberOfTimesIssued(userBookEntity.getNumberOfTimesIssued() + 1);
                                        userBookEntity.setIssuedDate(LocalDate.now());
                                        userBookEntity.setReturnDate(LocalDate.now().plusDays(14));
                                        userBookEntityRepository.save(userBookEntity);
                                        bookStatus = new IssueBookStatus(bookId, "Issued", "Book Re-Issued");
                                    } else {
                                        // Book cannot be re-issued as it has already been issued max number of times
                                        bookStatus = new IssueBookStatus(bookId, "Not Issued",
                                                "Book already issued to the user for " + maxNumberOfTimesIssue + " times");
                                    }
                                } else {
                                    // This is the first time book is being issued
                                    // Issue the books to the user
                                    UserBookEntity userBookEntity = new UserBookEntity(userId, bookId, LocalDate.now(), LocalDate.now().plusDays(14), 1);
                                    userBookEntityRepository.save(userBookEntity);

                                    // Manage the number of issued copies
                                    BookStatusEntity bs = be.get().getBookStatus();
                                    bs.setNumberOfCopiesIssued(bs.getNumberOfCopiesIssued() + 1);
                                    bookStatusRepository.save(bs);

                                    bookStatus = new IssueBookStatus(bookId, "Issued", "Book Issued");
                                }
                            }
                        }
                        issueBookStatuses.add(bookStatus);
                    });

            // Set and return final response
            return new IssueBookResponse(issueBookStatuses);
        } else {
            throw new LibraryResourceNotFoundException(traceId, "Library User Id: " + userId + " Not Found");
        }
    }  
	
}
