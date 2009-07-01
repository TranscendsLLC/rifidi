/**
 * 
 */
package org.rifidi.edge.core.sensors.exceptions;

/**
 * Thrown if a reader with the same name already exists.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class DuplicateSensorNameException extends Exception {

	/** Default serial version. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public DuplicateSensorNameException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 * @param cause
	 */
	public DuplicateSensorNameException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 */
	public DuplicateSensorNameException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 */
	public DuplicateSensorNameException(Throwable cause) {
		super(cause);
	}

}
