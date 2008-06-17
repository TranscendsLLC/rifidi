package org.rifidi.edge.core.exception.readerConnection;


public class RifidiConnectionIllegalStateException extends
		RifidiReaderPluginException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5573663418847045444L;

	public RifidiConnectionIllegalStateException() {
		super();
	}

	public RifidiConnectionIllegalStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public RifidiConnectionIllegalStateException(String message) {
		super(message);
	}

	public RifidiConnectionIllegalStateException(Throwable cause) {
		super(cause);
	}

}
