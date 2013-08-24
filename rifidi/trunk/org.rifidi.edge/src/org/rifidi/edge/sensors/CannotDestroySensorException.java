/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.sensors;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 *
 */
public class CannotDestroySensorException extends Exception {

	/***/
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public CannotDestroySensorException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CannotDestroySensorException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public CannotDestroySensorException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public CannotDestroySensorException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
}
