package exception;

public class BookHasCopiesLendedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3807412378764058593L;
	private static final String MESSAGE_FORMAT = "{ \"Book with ID has copies lended\" : \"%s\" }";
	
	public BookHasCopiesLendedException(String bookId){
		super(String.format(MESSAGE_FORMAT, bookId));
	}
}
