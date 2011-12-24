/*
 * 
 * InUseException.java
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
