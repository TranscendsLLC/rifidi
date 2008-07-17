/**
 * 
 */
package org.rifidi.edge.core.exceptions;

/**
 * Exception thrown if a command could not be executed
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RifidiExecutionException extends RifidiException {

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = -2515394276444517563L;

	/**
	 * Create a new RifidiExecutionException
	 */
	public RifidiExecutionException() {
		super();
	}

	/**
	 * Create a new RifidiExecutionException
	 * 
	 * @param arg0
	 *            Message
	 * @param arg1
	 *            previous Exception
	 */
	public RifidiExecutionException(String arg0, Throwable arg1) {
		super(arg0, arg1);

	}

	/**
	 * Create a new RifidiExecutionException
	 * 
	 * @param arg0
	 *            Message
	 */
	public RifidiExecutionException(String arg0) {
		super(arg0);

	}

	/**
	 * Create a new RifidiExecutionException
	 * 
	 * @param arg0
	 *            previous Exception
	 */
	public RifidiExecutionException(Throwable arg0) {
		super(arg0);

	}

}
