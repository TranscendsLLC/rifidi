/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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
