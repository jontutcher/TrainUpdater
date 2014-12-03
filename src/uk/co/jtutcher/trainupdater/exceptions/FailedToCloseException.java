package uk.co.jtutcher.trainupdater.exceptions;

public class FailedToCloseException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2460513174632729652L;
	public FailedToCloseException(){
		super();
	}
	public FailedToCloseException(String message)
	{
		super(message);
	}
}