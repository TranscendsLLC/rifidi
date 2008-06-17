package org.rifidi.edge.core.exception.readerConnection;

import org.rifidi.edge.core.exception.RifidiException;


public class RifidiReaderPluginException extends RifidiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2946763040951318913L;

	public RifidiReaderPluginException() {

	}

	public RifidiReaderPluginException(String message) {
		super(message);
	}

	public RifidiReaderPluginException(Throwable cause) {
		super(cause);
	}

	public RifidiReaderPluginException(String message, Throwable cause) {
		super(message, cause);
	}

}
