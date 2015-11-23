package exception;

public class BookAlreadyExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4952371296745057547L;
	private static final String MESSAGE_FORMAT = "{ \"Book with ID already exists\" : \"%s\" }";
	
	public BookAlreadyExistsException(String bookId){
		super(String.format(MESSAGE_FORMAT, bookId));
	}
}
