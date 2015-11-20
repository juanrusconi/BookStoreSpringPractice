package exception;

public class BookAlreadyExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4952371296745057547L;
	private static final String MESSAGE_FORMAT = "Book with ID %s already exists";
	
	public BookAlreadyExistsException(Integer bookId){
		super(String.format(MESSAGE_FORMAT, bookId));
	}
}
