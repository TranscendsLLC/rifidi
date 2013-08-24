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
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class NoSuchSensorException extends Exception {

	/** Serial VersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public NoSuchSensorException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 * @param cause
	 */
	public NoSuchSensorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 */
	public NoSuchSensorException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 */
	public NoSuchSensorException(Throwable cause) {
		super(cause);
	}

}
