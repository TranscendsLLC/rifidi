/*
 *  RifidiIIllegialArgumentException.java
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
public class RifidiIIllegialArgumentException extends RifidiException {

	/**
	 * Generated Serial UID.
	 */
	private static final long serialVersionUID = 4842341628682955961L;

	public RifidiIIllegialArgumentException() {
	}

	public RifidiIIllegialArgumentException(String message) {
		super(message);
	}

	public RifidiIIllegialArgumentException(Throwable cause) {
		super(cause);
	}

	public RifidiIIllegialArgumentException(String message, Throwable cause) {
		super(message, cause);
	}

}
