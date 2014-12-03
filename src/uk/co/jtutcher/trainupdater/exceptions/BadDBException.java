package uk.co.jtutcher.trainupdater.exceptions;

public class BadDBException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4497644486815153307L;
	public BadDBException(){
		super();
	}
	public BadDBException(String message)
	{
		super(message);
	}
}