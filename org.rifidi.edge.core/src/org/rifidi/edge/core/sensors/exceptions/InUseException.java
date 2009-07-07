/**
 * 
 */
package org.rifidi.edge.core.sensors.exceptions;

/**
 * Thrown if changing a sensor that is currently in use is attempted.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class InUseException extends Exception {

	/** Serial Version UID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public InUseException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 * @param cause
	 */
	public InUseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 */
	public InUseException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 */
	public InUseException(Throwable cause) {
		super(cause);
	}

}
