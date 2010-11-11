/*
 * InvalidAwidMessageException.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.adapter.awid.awid2010.communication.messages;

/**
 * Thrown if a byte[] is not a valid Awid Message when parsing
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class InvalidAwidMessageException extends Exception {

	/***/
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public InvalidAwidMessageException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public InvalidAwidMessageException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public InvalidAwidMessageException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public InvalidAwidMessageException(Throwable arg0) {
		super(arg0);
	}

}
