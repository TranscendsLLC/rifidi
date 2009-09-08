/*
 * 
 * NoSuchSensorException.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.core.sensors.exceptions;

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
