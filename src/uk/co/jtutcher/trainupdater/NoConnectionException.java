package uk.co.jtutcher.trainupdater;

public class NoConnectionException extends Exception {
	public NoConnectionException(){
		super();
	}
	public NoConnectionException(String message)
	{
		super(message);
	}
}