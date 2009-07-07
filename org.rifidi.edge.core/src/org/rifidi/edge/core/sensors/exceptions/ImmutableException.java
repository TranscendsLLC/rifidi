/**
 * 
 */
package org.rifidi.edge.core.sensors.exceptions;

/**
 * Thrown if changing an immutable sensor is attempted.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ImmutableException extends Exception {

	/** Serial version UID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 */
	public ImmutableException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public ImmutableException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Constructor.
	 * 
	 * @param arg0
	 */
	public ImmutableException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor.
	 * 
	 * @param arg0
	 */
	public ImmutableException(Throwable arg0) {
		super(arg0);
	}

}
