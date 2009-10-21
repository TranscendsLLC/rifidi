/*
 * 
 * CannotProvisionException.java
 *  
 * Created:     Oct 1st, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.services.provisioning.exceptions;

/**
 * Exception to use if there was a problem when provisioning.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CannotProvisionException extends Exception {

	/***/
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public CannotProvisionException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CannotProvisionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public CannotProvisionException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public CannotProvisionException(Throwable arg0) {
		super(arg0);
	}

}
