package de.philipp1994.lunch.common;

public class LunchProviderException extends Exception {
	
	public static final LunchProviderException NO_LUNCH_TODAY = new LunchProviderException("No Lunch today");
	public static final LunchProviderException LUNCH_MENU_NOT_AVAILABLE_YET = new LunchProviderException("Lunch menu not available yet");

	private static final long serialVersionUID = -8062442701953383915L;

	public LunchProviderException(String msg) {
		super(msg);
	}
	
	public LunchProviderException(String msg, Throwable cause) {
		super(msg, cause);
	}
}