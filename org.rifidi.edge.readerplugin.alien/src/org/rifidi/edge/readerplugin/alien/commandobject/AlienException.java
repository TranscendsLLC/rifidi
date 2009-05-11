/*
 *  AlienException.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.alien.commandobject;

/**
 * An Exception class for the Alien plugin.  
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class AlienException extends Exception {

	/** SerialVersion ID*/
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for AlienException.  
	 */
	public AlienException() {
	}

	/**
	 * Constructor for AlienException.  
	 * 
	 * @param arg0	Message for the exception.  
	 */
	public AlienException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor for AlienException.  
	 * 
	 * @param arg0	Throwable for the exception.  
	 */
	public AlienException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Constructor for AlienException.  
	 * 
	 * @param arg0	Message for the exception.  
	 * @param arg1	Throwable for the exception.  
	 */
	public AlienException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
