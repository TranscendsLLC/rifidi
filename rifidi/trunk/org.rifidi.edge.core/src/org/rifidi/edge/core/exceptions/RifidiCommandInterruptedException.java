package org.rifidi.edge.core.exceptions;

/**
 * Exception thrown if a command was interrupted while it was executing
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RifidiCommandInterruptedException extends RifidiException {

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 67446974621690843L;

	/**
	 * Create a new RifidiCommandInterruptedException
	 */
	public RifidiCommandInterruptedException() {
		super();
	}

	/**
	 * Create a new RifidiCommandInterruptedException
	 * 
	 * @param arg0
	 *            Message
	 * @param arg1
	 *            previous Exception
	 */
	public RifidiCommandInterruptedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Create a new RifidiCommandInterruptedException
	 * 
	 * @param arg0
	 *            Message
	 */
	public RifidiCommandInterruptedException(String arg0) {
		super(arg0);
	}

	/**
	 * Create a new RifidiCommandInterruptedException
	 * 
	 * @param arg0
	 *            previous Exception
	 */
	public RifidiCommandInterruptedException(Throwable arg0) {
		super(arg0);
	}

}
