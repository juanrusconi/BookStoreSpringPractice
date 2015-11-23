package exception;

public class CollectionIsEmptyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8390627252790409822L;
	private static final String MESSAGE_FORMAT = "{ \"The collection does not contain any documents\" : \"%s\" }";
	
	public CollectionIsEmptyException(String collName){
		super(String.format(MESSAGE_FORMAT, collName));
	}
	
	
}
