/*
 * 
 * DuplicateSubscriptionException.java
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
 * Thrown if a given Object is already subscribed to the reader.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class DuplicateSubscriptionException extends Exception {

	/** Default serial version. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public DuplicateSubscriptionException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param arg0
	 */
	public DuplicateSubscriptionException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 * @param cause
	 */
	public DuplicateSubscriptionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 */
	public DuplicateSubscriptionException(Throwable cause) {
		super(cause);
	}

}
