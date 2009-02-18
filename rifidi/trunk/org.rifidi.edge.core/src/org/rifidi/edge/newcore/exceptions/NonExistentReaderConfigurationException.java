/**
 * 
 */
package org.rifidi.edge.newcore.exceptions;

/**
 * Thrown if somebody tried to use a reader that either doesn't exist or got
 * removed from the registry.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class NonExistentReaderConfigurationException extends Exception {

	/** Default serial version UID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public NonExistentReaderConfigurationException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 * @param cause
	 */
	public NonExistentReaderConfigurationException(String message,
			Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 */
	public NonExistentReaderConfigurationException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 */
	public NonExistentReaderConfigurationException(Throwable cause) {
		super(cause);
	}

}
