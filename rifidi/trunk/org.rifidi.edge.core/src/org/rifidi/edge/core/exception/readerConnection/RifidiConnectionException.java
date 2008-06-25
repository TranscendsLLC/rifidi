/*
 *  RifidiConnectionException.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.exception.readerConnection;


/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class RifidiConnectionException extends
		RifidiConnectionIllegalStateException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7368023304240295709L;

	public RifidiConnectionException() {

	}

	public RifidiConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public RifidiConnectionException(String message) {
		super(message);
	}

	public RifidiConnectionException(Throwable cause) {
		super(cause);
	}

}
