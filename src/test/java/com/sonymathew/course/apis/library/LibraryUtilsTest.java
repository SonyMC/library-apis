package com.sonymathew.course.apis.library;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sonymathew.course.apis.library.utils.LibraryUtils;

public class LibraryUtilsTest {

	@Test
	public void doesStringValueExist() {
		assertTrue(LibraryUtils.doesStringValueExist("ValueExists"));
		assertFalse(LibraryUtils.doesStringValueExist(" "));
		assertFalse(LibraryUtils.doesStringValueExist(null));
		
	}

}
