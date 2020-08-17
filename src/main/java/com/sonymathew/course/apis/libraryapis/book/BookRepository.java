package com.sonymathew.course.apis.libraryapis.book;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CrudRepository<BookEntity, Integer>{
	
	List<BookEntity> findByTitle(String title); 
	
	List<BookEntity> findByTitleContaining(String title);
	
	BookEntity findByIsbn(String isbn);
	
	


}
