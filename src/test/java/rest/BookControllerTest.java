package rest;

import static org.junit.Assert.*;

import org.junit.Test;

import controller.BookController;
import exception.BookDoesNotExistException;

public class BookControllerTest {

	@Test
	public void test() throws BookDoesNotExistException {
		BookController controller = new BookController();
		
		assertEquals(controller.getBook("1").getBody().getTitle(), "first");
		
	}

}
