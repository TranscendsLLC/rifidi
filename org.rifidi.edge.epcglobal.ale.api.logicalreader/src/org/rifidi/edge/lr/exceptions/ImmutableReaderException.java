/**
 * 
 */
package org.rifidi.edge.lr.exceptions;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ImmutableReaderException extends Exception {

	/**
	 * Serial veriosn UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public ImmutableReaderException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ImmutableReaderException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ImmutableReaderException(String message, Throwable cause) {
		super(message, cause);
	}

}
