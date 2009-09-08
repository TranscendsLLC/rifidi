/*
 * 
 * NoReaderAvailableException.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */

package org.rifidi.edge.core.exceptions;

/**
 * Thrown if a sensorSession is not available.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class NoReaderAvailableException extends Exception {

	/** Default serial version. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.  
	 */
	public NoReaderAvailableException() {
		super();
	}

	/**
	 * Constructor.  
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public NoReaderAvailableException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Constructor.  
	 * 
	 * @param arg0
	 */
	public NoReaderAvailableException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor.  
	 * 
	 * @param arg0
	 */
	public NoReaderAvailableException(Throwable arg0) {
		super(arg0);
	}

}
