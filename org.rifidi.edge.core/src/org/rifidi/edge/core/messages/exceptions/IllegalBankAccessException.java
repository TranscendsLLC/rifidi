/*
 *  IllegalBankAccessException.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.core.messages.exceptions;
//TODO: Comments
/**
 * This exception is thrown when someone tries to access memory in an illegal
 * way
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class IllegalBankAccessException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public IllegalBankAccessException() {
	}

	/**
	 * @param arg0
	 */
	public IllegalBankAccessException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public IllegalBankAccessException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public IllegalBankAccessException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
