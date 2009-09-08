/*
 * 
 * AlienException.java
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
package org.rifidi.edge.readerplugin.alien.commandobject;

/**
 * An Exception that is thrown when reading interacting with an Alien Reader
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class AlienException extends Exception {

	/** SerialVersion FACTORY_ID */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public AlienException() {
	}

	/**
	 * @param arg0
	 */
	public AlienException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public AlienException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public AlienException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
