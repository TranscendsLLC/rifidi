package org.rifidi.edge.core.exceptions;

/**
 * Exception thrown if a Connection could not be established or failed
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RifidiConnectionException extends RifidiException {

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 5772687035577607204L;

	/**
	 * Create a new RifidiConnectionException
	 */
	public RifidiConnectionException() {
		super();
	}

	/**
	 * Create a new RifidiConnectionException
	 * 
	 * @param arg0
	 *            Message
	 * @param arg1
	 *            previous Exception
	 */
	public RifidiConnectionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Create a new RifidiConnectionException
	 * 
	 * @param arg0
	 *            Message
	 */
	public RifidiConnectionException(String arg0) {
		super(arg0);
	}

	/**
	 * Create a new RifidiConnectionException
	 * 
	 * @param arg0
	 *            previous Exception
	 */
	public RifidiConnectionException(Throwable arg0) {
		super(arg0);
	}

}
