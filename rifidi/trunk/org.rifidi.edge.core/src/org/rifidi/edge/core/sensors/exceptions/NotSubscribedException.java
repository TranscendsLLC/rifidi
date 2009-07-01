/**
 * 
 */
package org.rifidi.edge.core.sensors.exceptions;

/**
 * Thrown if an unsubscribe request is received for an object that is not
 * subscribed.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class NotSubscribedException extends Exception {

	/** Default serial version. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public NotSubscribedException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 * @param cause
	 */
	public NotSubscribedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 */
	public NotSubscribedException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 */
	public NotSubscribedException(Throwable cause) {
		super(cause);
	}

}
