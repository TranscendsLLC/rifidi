package org.rifidi.edge.core.exceptions;

/**
 * This exception should be thrown by a Communication Protocol when the message
 * that it is trying to convert bytes->object or object->bytes
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RifidiInvalidMessageFormat extends RifidiException {

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = -8367649195678157160L;

	/**
	 * Create a new RifidiInvalidMessageFormat
	 */
	public RifidiInvalidMessageFormat() {
		super();
	}

	/**
	 * Create a new RifidiInvalidMessageFormat
	 * 
	 * @param arg0
	 *            Message
	 * @param arg1
	 *            previous Exception
	 */
	public RifidiInvalidMessageFormat(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Create a new RifidiInvalidMessageFormat
	 * 
	 * @param arg0
	 *            Message
	 */
	public RifidiInvalidMessageFormat(String arg0) {
		super(arg0);
	}

	/**
	 * Create a new RifidiInvalidMessageFormat
	 * 
	 * @param arg0
	 *            previous Exception
	 */
	public RifidiInvalidMessageFormat(Throwable arg0) {
		super(arg0);
	}
}
