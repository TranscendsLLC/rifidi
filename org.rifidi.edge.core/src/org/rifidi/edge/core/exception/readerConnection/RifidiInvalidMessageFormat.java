package org.rifidi.edge.core.exception.readerConnection;


/**
 * This exception is thrown when the Protocol couldn't translate the bytes to a
 * Message
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RifidiInvalidMessageFormat extends RifidiReaderPluginException {

	/**
	 * SerialID
	 */
	private static final long serialVersionUID = -2386436632063476735L;

	public RifidiInvalidMessageFormat() {
	}

	public RifidiInvalidMessageFormat(String message) {
		super(message);
	}

	public RifidiInvalidMessageFormat(Throwable cause) {
		super(cause);
	}

	public RifidiInvalidMessageFormat(String message, Throwable cause) {
		super(message, cause);
	}

}
