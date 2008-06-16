package org.rifidi.edge.core.exception;

public class RifidiException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6039685993579708974L;

	public RifidiException() {
	}

	public RifidiException(String message) {
		super(message);
	}

	public RifidiException(Throwable cause) {
		super(cause);
	}

	public RifidiException(String message, Throwable cause) {
		super(message, cause);
	}

}
