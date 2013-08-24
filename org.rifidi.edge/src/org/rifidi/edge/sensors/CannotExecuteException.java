/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.sensors;

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
