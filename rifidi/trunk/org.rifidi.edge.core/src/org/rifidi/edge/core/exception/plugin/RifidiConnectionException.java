package org.rifidi.edge.core.exception.readerPlugin;


public class RifidiConnectionException extends
		RifidiAdapterIllegalStateException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7368023304240295709L;

	public RifidiConnectionException() {

	}

	public RifidiConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public RifidiConnectionException(String message) {
		super(message);
	}

	public RifidiConnectionException(Throwable cause) {
		super(cause);
	}

}
