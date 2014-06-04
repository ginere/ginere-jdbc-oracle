package eu.ginere.jdbc.oracle;

public class DaoManagerException extends Exception {

	private static final long serialVersionUID = 1L;

	public DaoManagerException(String message, Throwable cause){
		super(message,cause);
	}
	
	public DaoManagerException(String message){
		super(message);
	}
}
