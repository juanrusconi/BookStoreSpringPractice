package exception;

public class BookDoesNotExistException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6072303492205783293L;
	private static final String MESSAGE_FORMAT = "Book with ID %s does not exist";
	
	public BookDoesNotExistException(Integer bookId){
		super(String.format(MESSAGE_FORMAT, bookId));
	}
}
