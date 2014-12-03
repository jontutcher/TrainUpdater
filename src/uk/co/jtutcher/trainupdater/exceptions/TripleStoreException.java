package uk.co.jtutcher.trainupdater.exceptions;

public class TripleStoreException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6098281992938908844L;
	public TripleStoreException(){
		super();
	}
	public TripleStoreException(String message)
	{
		super(message);
	}
}