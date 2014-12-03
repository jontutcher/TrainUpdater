package uk.co.jtutcher.trainupdater.exceptions;

public class NoConnectionException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3196444578332763956L;
	public NoConnectionException(){
		super();
	}
	public NoConnectionException(String message)
	{
		super(message);
	}
}