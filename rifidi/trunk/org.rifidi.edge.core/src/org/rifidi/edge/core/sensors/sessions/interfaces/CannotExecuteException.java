/**
 * 
 */
package org.rifidi.edge.core.sensors.sessions.interfaces;

/**
 * This Exception is used when a method on the session cannot execute.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CannotExecuteException extends Exception {

	/***/
	private static final long serialVersionUID = -1L;

	/**
	 * 
	 */
	public CannotExecuteException() {
	}

	/**
	 * @param message
	 */
	public CannotExecuteException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CannotExecuteException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CannotExecuteException(String message, Throwable cause) {
		super(message, cause);
	}

}
