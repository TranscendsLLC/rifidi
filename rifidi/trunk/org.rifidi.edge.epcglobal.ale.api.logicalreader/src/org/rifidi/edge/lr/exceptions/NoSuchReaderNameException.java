/**
 * 
 */
package org.rifidi.edge.lr.exceptions;

/**
 * Thrown if somebody queried for a nonexistent reader.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class NoSuchReaderNameException extends Exception {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public NoSuchReaderNameException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public NoSuchReaderNameException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NoSuchReaderNameException(Throwable cause) {
		super(cause);
	}

}
