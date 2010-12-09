/*
 * CommandSubmissionException.java
 * 
 * Created:     July 22nd, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.api;

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
