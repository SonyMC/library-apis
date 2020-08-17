package com.sonymathew.course.apis.libraryapis.book;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.sonymathew.course.apis.libraryapis.author.Author;
import com.sonymathew.course.apis.libraryapis.author.AuthorEntity;
import com.sonymathew.course.apis.libraryapis.author.AuthorRepository;
import com.sonymathew.course.apis.libraryapis.exception.BookAlreadyExistsException;
import com.sonymathew.course.apis.libraryapis.exception.BookNotDeletedException;
import com.sonymathew.course.apis.libraryapis.exception.BookNotFoundException;
import com.sonymathew.course.apis.libraryapis.exception.BookNotUpdatedException;
import com.sonymathew.course.apis.libraryapis.exception.LibraryResourceNotFoundException;
import com.sonymathew.course.apis.libraryapis.exception.PublisherNotFoundException;
import com.sonymathew.course.apis.libraryapis.publisher.PublisherEntity;
import com.sonymathew.course.apis.libraryapis.publisher.PublisherRepository;
import com.sonymathew.course.apis.libraryapis.utils.LibraryApiUtils;

@Service
public class BookService {

	private static Logger logger = LoggerFactory.getLogger(BookService.class);

	private BookRepository bookRepository;

	private PublisherRepository publisherRepository;

	private BookStatusRepository bookStatusRepository;

	private AuthorRepository authorRepository;

	// Constructor based dependency injection
	public BookService(BookRepository bookRepository, PublisherRepository publisherRepository,
			BookStatusRepository bookStatusRepository, AuthorRepository authorRepository) {
		this.bookRepository = bookRepository;
		this.publisherRepository = publisherRepository;
		this.bookStatusRepository = bookStatusRepository;
		this.authorRepository = authorRepository;
	}

	/**
	 * 
	 * 
	 * ** Utility Methods Start
	 * ******************************************************************************************************************
	 * 
	 */
	// Utility method to be used further downstream which creates Book Object
	// from Book Entity
	private Book createBookFromEntity(BookEntity bookEntity) {

		Book createdBookFromEntity = new Book(bookEntity.getBookId(), bookEntity.getIsbn(), bookEntity.getTitle(),
				bookEntity.getPublisher().getPublisherid(), bookEntity.getYearPublished(), bookEntity.getEdition(),
				createBookStatusFromEntity(bookEntity.getBookStatus()));

		// Now check if author is available before extracting it from book
		// entity and adding it to the Book Object.
		if (bookEntity.getAuthors() != null && bookEntity.getAuthors().size() > 0) {
			Set<Author> authorSet = getAuthorSet(bookEntity);
			// Now add the author to the book object
			createdBookFromEntity.setAuthors(authorSet);
		}

		return createdBookFromEntity;

	}


	// Utility method to be used further downstream which creates Book Entity
	// from Book Object
	private BookEntity createBookEntity(Book bookObject) {

		// The book entity will be used to populate/update the DB table

/*
 *  We cannot call use a constructor with a Book Status as that will require a book id which at this point is not available!!!

		BookEntity createdBookEntityFromObject = new BookEntity(bookObject.getIsbn(), bookObject.getTitle(),
				bookObject.getYearPublished(), bookObject.getEdition(),
				publisherRepository.findById(bookObject.getPublisherId()).get(),
				createBookStatusEntityFromObject(bookObject.getBookStatus()));

		
		 * 		
 */
		BookEntity createdBookEntityFromObject = new BookEntity(
												bookObject.getIsbn(), 
												bookObject.getTitle(),
												bookObject.getYearPublished(),
												bookObject.getEdition(),
												publisherRepository.findById(bookObject.getPublisherId()).get());
	

		// Now check if author is available before extracting it from book
		// object and later adding it to the Book Entity.
		if (bookObject.getAuthors() != null && bookObject.getAuthors().size() > 0) {
			Set<AuthorEntity> authorEntitySet = getAuthorEntitySet(bookObject);
			// Now add the author to the book entity
			createdBookEntityFromObject.setAuthors(authorEntitySet);

		}

		return createdBookEntityFromObject;
	}
	
	
// Utility method to extract set of authors from the Book Object
	private Set<AuthorEntity> getAuthorEntitySet(Book bookObject) {
		Set<AuthorEntity> authorEntitySet = // authorEntitySet is by default a
											// final variable as it is the
											// result of a lambda function;
											// hence we cannot declare
											// 'Set<AuthorEntity>
											// authorEntitySet' separately and
											// then modify it using lambda.It
											// has to be this way!!!
				bookObject.getAuthors() // Thsi will be a HashSet containing
										// Authors
						.stream().map(authorObject -> {
							AuthorEntity authorEntityFromRep = authorRepository.findById(authorObject.getAuthorId())
									.get(); // findById will return an optional
											// and hence we have to get
							return authorEntityFromRep;
						}).collect(Collectors.toSet());
		return authorEntitySet;
	}

	// Utility method to extract set of authors from the BookEntity
	private Set<Author> getAuthorSet(BookEntity bookEntity) {
		Set<Author> authorSet = // authorSet is by default a final variable as
								// it is the result of a lambda function;
				bookEntity.getAuthors() // this will be a Hash set containing
										// Author Entities
						.stream().map(authorEntity -> {
							AuthorEntity authorEntityFromRep = authorRepository.findById(authorEntity.getAuthorId())
									.get();// findById will return optional and
											// hence we have to get the author
											// entity
							return createAuthorFromAuthorEntity(authorEntityFromRep);
						}).collect(Collectors.toSet());

		return authorSet;
	}

	// Utility method to create author from Author entity
	private Author createAuthorFromAuthorEntity(AuthorEntity authorEntity) {
		Author author = new Author(authorEntity.getAuthorId(), authorEntity.getFirstName(), authorEntity.getLastName());
		return author;
	}

	// Utility method to create Author entity from Author Object
	// Commenting this out since we are not suing it !!!
/*
	private AuthorEntity createAuthorEntityFromAuthor(Author author) {
		AuthorEntity authorEntity = new AuthorEntity(author.getFirstName(), author.getLastName());
		return authorEntity;
	}
 */	

	// Utility method to be used further downstream which creates book status
	// object from book status entity
	private BookStatus createBookStatusFromEntity(BookStatusEntity bookStatusEntity) {

		BookStatus bookStatus = new BookStatus(bookStatusEntity.getBookId(),
											   bookStatusEntity.getState(), 
											   bookStatusEntity.getTotalNumberOfCopies(),
											   bookStatusEntity.getNumberOfCopiesIssued());

		return bookStatus;
	}

	// Utility method to be used further downstream which creates book status
	// entity from book status object
	private BookStatusEntity createBookStatusEntityFromObject(BookStatus bookStatus) {

		BookStatusEntity bookStatusEntity = new BookStatusEntity(bookStatus.getBookId(), bookStatus.getState(),
				bookStatus.getTotalNumberOfCopies(), bookStatus.getNumberOfCopiesIssued());

		return bookStatusEntity;

	}

	// utility method to convert Book Entity List to Book Object list
	private List<Book> createBooksForSearchResponse(List<BookEntity> bookEntityList) {

		return bookEntityList.stream().map(bookEntity -> createBookFromEntity(bookEntity)).collect(Collectors.toList());
	}

	/**
	 * 
	 * 
	 * ** Utility Methods End
	 * ****************************************************************************************************************
	 * 
	 */

	// Add a new book entity
	@Transactional   // use this when we want to use multiple DB transactions( in this case we tried with two saves)  as a singe unit
	public void addBook(Book bookToBeAdded, String traceId)
			throws BookAlreadyExistsException, PublisherNotFoundException {

		logger.debug("Trace ID: *** {}, Request to add Book: {}", traceId, bookToBeAdded);

		// Create book entity from book object in request
		BookEntity bookEntity = createBookEntity(bookToBeAdded);

		//Get the parent of the Book (Publisher is the parent). If not found throw an exception
		// Book has one-to-many relationship with publisher(i.e. Publisher is
		// the parent as one publisher can have many books. So we have to get
		// the publisher from the book, attach it to teh book entoty and then
		// save teh book entity
		Optional<PublisherEntity> optPublisherEntity = publisherRepository.findById(bookToBeAdded.getPublisherId());

		if (optPublisherEntity.isPresent()) {
			PublisherEntity publisherEntity = optPublisherEntity.get();
			bookEntity.setPublisher(publisherEntity);
		} else {
			logger.error("Trace ID :{},*** Publisher mentioned for the book does not exist!!!", traceId);
			throw new PublisherNotFoundException(traceId, "Publisher mentioned for the book does not exist!!!");
		}

		// Once we have set the publihser, we have to save the book entity to
		// the repository
		// This is the entity book that will used to save to the DB table using
		// the book entity we just created above
		BookEntity addedBookEntity = null;
		try {
			addedBookEntity = bookRepository.save(bookEntity);
		} catch (DataIntegrityViolationException divExc) {
			// The below logging statement is refactored using log4j
			// logger.error("Trace ID :*** " + traceId + " ***Duplicate...Book
			// already exists, try another name!!!", divExc);
			logger.error("Trace ID :{},*** Duplicate...Book already exists, try another name!!!", traceId, divExc);
			throw new BookAlreadyExistsException(traceId,
					"Trace ID :*** " + traceId + " ***Duplicate...Book already exists, try another name!!!");
		}

		// Once the book entity is saved, we have to manage the one-to-one
		// relationship with the book status repoository
		// For this , first create the book status entity
		BookStatusEntity bookStatusEntity = new BookStatusEntity(addedBookEntity.getBookId(),
																 bookToBeAdded.getBookStatus().getState(),
																 bookToBeAdded.getBookStatus().getTotalNumberOfCopies(), 
																 0); 	// Since thsi is a new book, no. of copies will be 0						

		// The below step will not work as we indirectly this will try to save the book entity once more which throw up a null identidier Db error. 
		//bookStatusEntity.setBookEntity(bookEntity);

		// Now save the Book status entity to the Book Status repository.
		bookStatusRepository.save(bookStatusEntity);

		// Now set the id of the book object which will be used as part of the
		// response
		bookToBeAdded.setBookID(addedBookEntity.getBookId());

		// Also set the book status which will be used as part of the response
		bookToBeAdded.setBookStatus(createBookStatusFromEntity(bookStatusEntity));

		logger.info("Trace ID : {}, Sucess !!! Book added: {}", traceId, bookToBeAdded);

	}

	// Get the book entity by id
	public Book getBookbyId(int bookId, String traceId) throws BookNotFoundException {

		// This is the entity book used in the DB table. This is of type
		// optional because we are using this to hold the results from the JPA
		// API in BookRepository.class
		Optional<BookEntity> retrievedBook = null;

		try {
			retrievedBook = bookRepository.findById(bookId);
		} catch (DataIntegrityViolationException divExc) {
			// The below logging statement is refactored using log4j
			// logger.error("Trace ID :*** " + traceId + " *** Book not
			// Found!!Try with a different id...", divExc);
			logger.error("Trace ID :{}, *** Book not Found!!Try with a different id...", traceId, divExc);
			throw new BookNotFoundException(traceId,
					"Trace ID :*** " + traceId + " *** Book not Found!!Try with a different id...");

		}

		// If the optional is valid then save to a Book  object
		if (retrievedBook.isPresent()) {
			BookEntity bookEntity = retrievedBook.get();
			// Now create the book object from the retrieved entity.
			Book foundBook = createBookFromEntity(bookEntity);
			logger.info("Trace ID : {}, Hurray !!! Book Found By ID: {}", traceId, foundBook);
			return foundBook;
		} else {
			// The below logging statement is refactored using log4j
			// logger.error("Trace ID :*** " + traceId + " *** Book not
			// Found!!Try with a different id...");
			logger.error("Trace ID :{}, *** Book not Found!!Try with a different id...", traceId);
			throw new BookNotFoundException(traceId,
					"Trace ID :*** " + traceId + " *** Book ID " + bookId + " not Found!!Try with a different id...");
		}

	}

	// Get the book entity by Isbn
	public Book getBookbyIsbn(String isbn, String traceId) throws BookNotFoundException {


		try {
			BookEntity retrievedBook = bookRepository.findByIsbn(isbn);
			Book foundBook = createBookFromEntity(retrievedBook);
			logger.info("Trace ID : {}, Hurray !!! Book: {} Found By Isbn: {}", traceId, foundBook, isbn);
			return foundBook;
		} catch (DataIntegrityViolationException divExc) {
			// The below logging statement is refactored using log4j
			// logger.error("Trace ID :*** " + traceId + " *** Book not
			// Found!!Try with a different id...", divExc);
			logger.error("Trace ID :{}, *** Book not Found!!Try with a different Isbn...", traceId, divExc);
			throw new BookNotFoundException(traceId,
					"Trace ID :*** " + traceId + " *** Book not Found!!Try with a different Isbn...");

		}

	}
	
	
	// Get the book entity by name - method 1 where full name is passed in the
	// request path
	public List<Book> getBookbyTitle(String bookName, String traceId) throws BookNotFoundException {

		return findBookByTitle(bookName, "FullName", traceId);

	}

	// Get the book entity by name - method 2 where partial name is passed as a
	// query in uri
	public List<Book> searchBook(String bookName, String traceId) throws BookNotFoundException {

		return findBookByTitle(bookName, "PartialName", traceId);
	}

	private List<Book> findBookByTitle(String bookName, String searchMode, String traceId)
			throws BookNotFoundException {

		// Check if name is supplied
		if (!LibraryApiUtils.doesStringValueExist(bookName)) {
			// The below logging statement is refactored using log4j
			// logger.error("Trace ID :*** " + traceId + " *** Book name not
			// supplied !!!");
			logger.error("Trace ID :{}, *** Book name not supplied !!!", traceId);
			throw new BookNotFoundException(traceId, "Trace ID :*** " + traceId + " *** Book name not supplied !!!");
		}

		List<BookEntity> bookEntityList = new ArrayList<BookEntity>();

		// Entity list
		if (searchMode.matches("FullName")) {
			bookEntityList = bookRepository.findByTitle(bookName);
		} else {
			bookEntityList = bookRepository.findByTitleContaining(bookName);
		}

		if (bookEntityList.isEmpty()) {
			logger.error("Trace ID :{}, *** Book Title " + bookName + " not Found!!Try with different title...",
					traceId);
			throw new BookNotFoundException(traceId, "Trace ID :*** " + traceId + " *** Title name " + bookName
					+ " not Found!!Try with different title...");
		} else {
			logger.info("Trace ID : {}, Gotcha by Title !!! Book Found By Title", traceId);
			return createBooksForSearchResponse(bookEntityList);
			/*
			 * this is the long winded method compared to the short one we have
			 * above
			 * 
			 * for(BookEntity bookEntity:bookEntityList){
			 * bookList.add(createBookFromEntity(bookEntity)); }
			 * 
			 * return bookList;
			 * 
			 */

		}
	}

	// Update the book entity by id
	public void updateBookbyId(Book bookTobeUpdated, String traceId)
			throws BookNotFoundException, BookNotUpdatedException {

		// This is the entity book used in the DB table. This is of type
		// optional because we are using this to hold the results from teh JPA
		// API in BookRepository.class
		Optional<BookEntity> retrievedBookEntity = null;

		// Retrieve the Book Entity from the DB table
		try {
			retrievedBookEntity = bookRepository.findById(bookTobeUpdated.getBookId());
		} catch (DataIntegrityViolationException divExc) {
			// The below logging statement is refactored using log4j
			// logger.error("Trace ID :*** " + traceId + " *** Book ID " +
			// bookTobeUpdated.getBookID() + " not Found!!Try with a different
			// id...",divExc);
			logger.error("Trace ID :{}, *** Book ID " + bookTobeUpdated.getBookId()
					+ "not Found!!Try with a different id...", traceId, divExc);
			throw new BookNotFoundException(traceId, "Trace ID :*** " + traceId + " *** Book ID "
					+ bookTobeUpdated.getBookId() + " not Found!!Try with a different id...");

		}

		// In case nothing is rertieved...Probably this check is redundant..
		if (!retrievedBookEntity.isPresent()) {
			// The below logging statement is refactored using log4j
			// logger.error("Trace ID :*** " + traceId + " *** Book ID " +
			// bookTobeUpdated.getBookID() + " not Found!!Try with a different
			// id...");
			logger.error("Trace ID :{}, *** Book ID " + bookTobeUpdated.getBookId()
					+ "not Found!!Try with a different id...", traceId);
			throw new BookNotFoundException(traceId, "Trace ID :*** " + traceId + " *** Book ID "
					+ bookTobeUpdated.getBookId() + " not Found!!Try with a different id...");
		}

		// Now if optional is valid , set the values of the Book entity in DB
		// using values of the Book Object
		// map to book entity object
		BookEntity bookEntityMapped = retrievedBookEntity.get();

		// Check if the to be changed is valid and something actually has
		// changed and if changed set teh new values.

		boolean fieldChanged = false;

		// Title
		if (LibraryApiUtils.doesStringValueExist(bookEntityMapped.getTitle())
				&& bookEntityMapped.getTitle() != bookTobeUpdated.getTitle()) {
			bookEntityMapped.setTitle(bookTobeUpdated.getTitle());
			fieldChanged = true;
		}

		// Edition
		if (LibraryApiUtils.doesStringValueExist(bookEntityMapped.getEdition())
				&& bookEntityMapped.getEdition() != bookTobeUpdated.getEdition()) {
			bookEntityMapped.setEdition(bookTobeUpdated.getEdition());
			fieldChanged = true;
		}

		// Isbn
		if (LibraryApiUtils.doesStringValueExist(bookEntityMapped.getIsbn())
				&& bookEntityMapped.getIsbn() != bookTobeUpdated.getIsbn()) {
			bookEntityMapped.setIsbn(bookTobeUpdated.getIsbn());
			fieldChanged = true;
		}

		// Year publisher
		if (bookEntityMapped.getYearPublished() != bookTobeUpdated.getYearPublished()) {
			bookEntityMapped.setYearPublished(bookTobeUpdated.getYearPublished());
			fieldChanged = true;
		}

		// Author set
		if ((bookEntityMapped.getAuthors() != null) && (bookEntityMapped.getAuthors().size() > 0)
				&& (bookEntityMapped.getAuthors() != getAuthorEntitySet(bookTobeUpdated))) {
			bookEntityMapped.setAuthors(getAuthorEntitySet(bookTobeUpdated));
			fieldChanged = true;
		}

		// Book Status
		if ((bookEntityMapped.getBookStatus() != null) && (bookEntityMapped
				.getBookStatus() != createBookStatusEntityFromObject(bookTobeUpdated.getBookStatus()))) {
			bookEntityMapped.setBookStatus(createBookStatusEntityFromObject(bookTobeUpdated.getBookStatus()));
			fieldChanged = true;
		}

		// Publisher
		if (bookEntityMapped.getPublisher() != null
				&& (bookEntityMapped.getPublisher().getPublisherid() != bookTobeUpdated.getPublisherId())) {
			bookEntityMapped.setPublisher(publisherRepository.findById(bookTobeUpdated.getPublisherId()).get());
			fieldChanged = true;
		}

		// Finally update the database Book entity using the 'PublishRepository'
		// interface
		if (fieldChanged) { // Only update if something has changed and is valid
			try {
				bookRepository.save(bookEntityMapped);
				logger.info("Trace ID : {}, Jolly...Updated the Book: {}", traceId, bookEntityMapped);
			} catch (DataIntegrityViolationException divExc) {
				// The below logging statement is refactored using log4j
				// logger.error("Trace ID :*** " + traceId + " ***
				// Blimey...something went wrong...contact admin and say
				// SOS!!!",divExc);
				logger.error("Trace ID :{}, *** Blimey...something went wrong...contact admin and say SOS!!!", traceId);
				throw new BookNotUpdatedException(traceId, "Trace ID :*** " + traceId
						+ " *** Blimey...something went wrong...contact admin and say SOS!!!");
			}
		}

	}

	// Delete the book entity by id
	public void deleteBookbyId(int bookId, String traceId) throws BookNotFoundException, BookNotDeletedException {

		// This is the entity book used in the DB table. This is of type
		// optional because we are using this to hold the results from teh JPA
		// API in BookRepository.class
		Optional<BookEntity> retrievedBookEntity = null;

		// Retrieve the Book Entity from the DB table
		try {
			retrievedBookEntity = bookRepository.findById(bookId);
		} catch (DataIntegrityViolationException divExc) {
			// The below logging statement is refactored using log4j
			// logger.error("Trace ID :*** " + traceId + " *** Book ID " +
			// bookId + " not Found!!Try with a different id...", divExc);
			logger.error("Trace ID :{}, *** Book ID " + bookId + "not Found!!Try with a different id...", traceId);
			throw new BookNotFoundException(traceId,
					"Trace ID :*** " + traceId + " *** Book ID " + bookId + " not Found!!Try with a different id...");

		}

		// In case nothing is retrieved...Probably this check is redundant
		if (!retrievedBookEntity.isPresent()) {
			// The below logging statement is refactored using log4j
			// logger.error("Trace ID :*** " + traceId + " *** Book ID " +
			// bookId + " not Found!!Try with a different id...");
			logger.error("Trace ID :{}, *** Book ID " + bookId + "not Found!!Try with a different id...", traceId);
			throw new BookNotFoundException(traceId,
					"Trace ID :*** " + traceId + " *** Book ID " + bookId + " not Found!!Try with a different id...");
		}

		// map to book entity object
		BookEntity bookEntityToBeDeleted = retrievedBookEntity.get();

		// Finally delete the book entity from the database using the
		// 'PublishRepository' interface
		try {

			bookRepository.delete(bookEntityToBeDeleted);
			logger.info("Trace ID : {}, Target terminated: {}", traceId, bookEntityToBeDeleted);
		} catch (DataIntegrityViolationException divExc) {
			// The below logging statement is refactored using log4j
			// logger.error("Trace ID :*** " + traceId + " *** Will die another
			// day...Get professional help!!!",divExc);
			logger.error("Trace ID :{}, *** *** Will die another day...Get professional help!!!", traceId, divExc);
			throw new BookNotDeletedException(traceId,
					"Trace ID :*** " + traceId + " *** Will die another day...Get professional help!!!");
		}

	}

	
	// Add author to a book
	@Transactional  // single unit if DB work 
	public Book addBookAuthors(Integer bookId, Set<Integer> authorIds, String traceId) throws BookNotFoundException, LibraryResourceNotFoundException {
		
		//  First try retrieving the book from the repo using the book id
		Optional<BookEntity> optionalBookEntity = bookRepository.findById(bookId);
		
		if (!optionalBookEntity.isPresent()) {
			logger.error("Trace ID :{}, *** Book ID " + bookId + "not Found!!Try with a different id...", traceId);
			throw new BookNotFoundException(traceId,
					"Trace ID :*** " + traceId + " *** Book ID " + bookId + " not Found!!Try with a different id...");
		}else{ /// Book has been found 
			BookEntity retrievedBookEntity = optionalBookEntity.get();
			//Now map the author set by retrievign teh authors usin the author id set in the request 
			Set<AuthorEntity> authorEntitiesSet = authorIds
				     										.stream()  // is a set , so we can stream 
				     										.map(authorId  -> authorRepository.findById(authorId))   // get authors from repo 
				     										.collect(Collectors.toSet())   // create a new Author name set 
				     										.stream()    // stream the Author name set again for further processing 
				     										.filter(authorEntity -> authorEntity.isPresent() == true)  // check the author entity is present in the set
				     										.map(authorEntity -> authorEntity.get())   // get the author entoties from the filtered set above 
				     										.collect(Collectors.toSet()); // finally collect the Author entity set..whew!!!!!
			
			if(authorEntitiesSet.size() != authorIds.size()){   // Now in case  author entities is not extraced for all author ids, we would need to extract the author ids as string for logging 
				String authorsNotMatchedList = authorIds
											.stream()
											.map(authorId  -> authorId.toString().concat(","))    // convert each integer author id to string and append a comma 
										    .reduce(",", String::concat);  // use method reference to concat all author ids seperated by a comma to a string
				logger.debug("Trace ID :{}, *** *** Some authors were not found!!!" + authorsNotMatchedList, traceId);
			}
			
			
			if(authorEntitiesSet.size() == 0) {   // In case no entities were extracted , we would need to extract the author ids as string for error processing 
				String authorsNotFoundList = authorIds
											.stream()
											.map(authorId  -> authorId.toString().concat(","))    // convert each integer author id to string and append a comma 
										    .reduce(",", String::concat);  // use method reference to concat all author ids seperated by a comma to a string
				logger.error("Trace ID :{}, *** *** None of the authors were found!!!", traceId);
				throw new LibraryResourceNotFoundException(traceId, "BookId : " + bookId + " - None of the authors were found !!!" + authorsNotFoundList);
			}			
			
			// Now add the author entitty set to the rterieved book entity
			retrievedBookEntity.setAuthors(authorEntitiesSet);
			
			//save the updated book 
			bookRepository.save(retrievedBookEntity);
			
			//Now return the Book object by creqating it from the Book entity
			return createBookFromEntity(retrievedBookEntity);
			
		}
		
		
		

		
	}

}
