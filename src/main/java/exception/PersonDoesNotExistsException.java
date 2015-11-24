package exception;

public class PersonDoesNotExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5632822880389546411L;
	private static final String MESSAGE_FORMAT = "{ 'Person with this name does not exist' : '%s' }";
	
	public PersonDoesNotExistsException(String name){
		super(String.format(MESSAGE_FORMAT, name));
	}
}
