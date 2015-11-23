package exception;

public class BookDoesNotExistException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6072303492205783293L;
	private static final String MESSAGE_FORMAT = "{ \"Book with ID does not exist\" : \"%s\" }";
	
	public BookDoesNotExistException(String bookId){
		super(String.format(MESSAGE_FORMAT, bookId));
	}
}
