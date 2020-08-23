package com.sonymathew.course.apis.libraryapis.user;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UserBookEntityRepository  extends CrudRepository<UserBookEntity, Integer>{
	List<UserBookEntity> findByUserIdAndBookId(int userId, int bookId);

}
