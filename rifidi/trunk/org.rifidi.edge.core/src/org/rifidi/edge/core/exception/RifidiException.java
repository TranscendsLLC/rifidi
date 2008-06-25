/*
 *  RifidiException.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.exception;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class RifidiException extends Exception {

	/**
	 * Serial UID.  
	 */
	private static final long serialVersionUID = -6039685993579708974L;


	public RifidiException() {
	}

	public RifidiException(String message) {
		super(message);
	}

	public RifidiException(Throwable cause) {
		super(cause);
	}

	public RifidiException(String message, Throwable cause) {
		super(message, cause);
	}

}
