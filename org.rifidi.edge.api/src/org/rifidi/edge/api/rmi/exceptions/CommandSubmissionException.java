/**
 * 
 */
package org.rifidi.edge.api.rmi.exceptions;

/**
 * This is an exception that is thrown if there was a problem when submitting
 * command for execution to a reader session
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CommandSubmissionException extends Exception {
	
	/** Default ID*/
	private static final long serialVersionUID = 1L;  

	/**
	 * 
	 */
	public CommandSubmissionException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public CommandSubmissionException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public CommandSubmissionException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CommandSubmissionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
