package exception;

public class PersonDoesNotExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5632822880389546411L;
	private static final String MESSAGE_FORMAT = "{ \"Person with (last name, first name) does not exist\" : \"%s, %s\" }";
	
	public PersonDoesNotExistsException(String fname, String lname){
		super(String.format(MESSAGE_FORMAT, lname, fname));
	}
}
