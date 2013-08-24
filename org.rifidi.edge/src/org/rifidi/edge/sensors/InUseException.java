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
package org.rifidi.edge.sensors;

/**
 * Thrown if changing a sensor that is currently in use is attempted.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class InUseException extends Exception {

	/** Serial Version UID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public InUseException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 * @param cause
	 */
	public InUseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 */
	public InUseException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 */
	public InUseException(Throwable cause) {
		super(cause);
	}

}
