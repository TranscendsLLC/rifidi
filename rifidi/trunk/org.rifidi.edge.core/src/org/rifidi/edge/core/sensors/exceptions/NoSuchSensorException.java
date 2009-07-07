/**
 * 
 */
package org.rifidi.edge.core.sensors.exceptions;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class NoSuchSensorException extends Exception {

	/** Serial VersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public NoSuchSensorException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 * @param cause
	 */
	public NoSuchSensorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 */
	public NoSuchSensorException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 */
	public NoSuchSensorException(Throwable cause) {
		super(cause);
	}

}
