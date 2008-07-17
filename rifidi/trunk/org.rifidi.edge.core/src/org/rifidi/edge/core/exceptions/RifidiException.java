package org.rifidi.edge.core.exceptions;

/**
 * Exception thrown if a unexpected event occurred. This is the Root of all
 * RifidiExceptions.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RifidiException extends Exception {

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 277183065392166915L;

	/**
	 * Create a new RifidiExcpetion
	 */
	public RifidiException() {
		super();
	}

	/**
	 * Create a new RifidiException
	 * 
	 * @param arg0
	 *            Message
	 * @param arg1
	 *            previous Exception
	 */
	public RifidiException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Create a new RifidiException
	 * 
	 * @param arg0
	 *            Message
	 */
	public RifidiException(String arg0) {
		super(arg0);
	}

	/**
	 * Create a new RifidiException
	 * 
	 * @param arg0
	 *            previous Exception
	 */
	public RifidiException(Throwable arg0) {
		super(arg0);
	}

}
