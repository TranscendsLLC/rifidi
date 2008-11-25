package org.rifidi.edge.client.connections.exceptions;

/**
 * Thrown if there was a problem getting or setting properties on the remote
 * reader session
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CannotExecutePropertyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CannotExecutePropertyException() {
		// TODO Auto-generated constructor stub
	}

	public CannotExecutePropertyException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public CannotExecutePropertyException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public CannotExecutePropertyException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
