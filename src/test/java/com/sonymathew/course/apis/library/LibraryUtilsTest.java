package com.sonymathew.course.apis.library;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sonymathew.course.apis.libraryapis.utils.*;

public class LibraryUtilsTest {

	@Test
	public void doesStringValueExist() {
		assertTrue(LibraryApiUtils.doesStringValueExist("ValueExists"));
		assertFalse(LibraryApiUtils.doesStringValueExist(" "));
		assertFalse(LibraryApiUtils.doesStringValueExist(null));
		
	}

}
