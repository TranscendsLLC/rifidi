/*
 *  RifidiPreviousError.java
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

public class RifidiPreviousErrorException extends RifidiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7052195193847631535L;

	public RifidiPreviousErrorException() {
	}

	public RifidiPreviousErrorException(String message) {
		super(message);
	}

	public RifidiPreviousErrorException(Throwable cause) {
		super(cause);
	}

	public RifidiPreviousErrorException(String message, Throwable cause) {
		super(message, cause);
	}

}
