
package org.rifidi.edge.core.messages.exceptions;

/**
 * This exception is thrown when someone tries to access memory in an illegal
 * way.  
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class IllegalBankAccessException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.  
	 */
	public IllegalBankAccessException() {
	}

	/**
	 * Constructor.  
	 * 
	 * @param arg0
	 */
	public IllegalBankAccessException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor.  
	 * 
	 * @param arg0
	 */
	public IllegalBankAccessException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Constructor.  
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public IllegalBankAccessException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
