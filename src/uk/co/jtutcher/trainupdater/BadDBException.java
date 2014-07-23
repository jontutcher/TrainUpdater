package uk.co.jtutcher.trainupdater;

public class BadDBException extends Exception {
	public BadDBException(){
		super();
	}
	public BadDBException(String message)
	{
		super(message);
	}
}