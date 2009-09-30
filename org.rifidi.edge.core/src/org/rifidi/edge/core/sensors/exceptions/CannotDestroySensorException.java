/*
 * 
 * CannotDestroySensorException.java
 *  
 * Created:     Sep 30th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.sensors.exceptions;

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
