/**
 * 
 */
package org.rifidi.edge.core.exceptions;

/**
 * Thrown if a reader is not available.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class NoReaderAvailableException extends Exception {

	/** Default serial version. */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public NoReaderAvailableException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public NoReaderAvailableException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public NoReaderAvailableException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public NoReaderAvailableException(Throwable arg0) {
		super(arg0);
	}

}
