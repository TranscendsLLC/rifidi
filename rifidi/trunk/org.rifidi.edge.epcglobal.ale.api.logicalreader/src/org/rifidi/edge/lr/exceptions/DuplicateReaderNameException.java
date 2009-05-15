/**
 * 
 */
package org.rifidi.edge.lr.exceptions;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class DuplicateReaderNameException extends Exception {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public DuplicateReaderNameException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DuplicateReaderNameException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DuplicateReaderNameException(String message, Throwable cause) {
		super(message, cause);
	}

}
