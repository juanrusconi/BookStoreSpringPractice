package exception;

public class PersonAlreadyExistsException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2996794876919369635L;
	private static final String MESSAGE_FORMAT = "{ 'Person with this name already exists' : '%s' }";
	
	public PersonAlreadyExistsException(String name){
		super(String.format(MESSAGE_FORMAT, name));
	}
}
