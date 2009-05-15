/**
 * 
 */
package org.rifidi.edge.lr.exceptions;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ReaderInUseException extends Exception {

	/**
	 * Serial versionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public ReaderInUseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ReaderInUseException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ReaderInUseException(Throwable cause) {
		super(cause);
	}

}
