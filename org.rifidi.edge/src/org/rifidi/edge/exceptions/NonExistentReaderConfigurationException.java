/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.exceptions;

/**
 * Thrown if somebody tried to use a sensorSession that either doesn't exist or got
 * removed from the registry.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class NonExistentReaderConfigurationException extends Exception {

	/** Default serial version UID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public NonExistentReaderConfigurationException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 * @param cause
	 */
	public NonExistentReaderConfigurationException(String message,
			Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 */
	public NonExistentReaderConfigurationException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 */
	public NonExistentReaderConfigurationException(Throwable cause) {
		super(cause);
	}

}
