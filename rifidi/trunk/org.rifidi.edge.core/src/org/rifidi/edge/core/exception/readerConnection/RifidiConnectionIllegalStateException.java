/*
 *  RifidiConnectionIllegalStateException.java
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


public class RifidiConnectionIllegalStateException extends
		RifidiReaderPluginException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5573663418847045444L;

	public RifidiConnectionIllegalStateException() {
		super();
	}

	public RifidiConnectionIllegalStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public RifidiConnectionIllegalStateException(String message) {
		super(message);
	}

	public RifidiConnectionIllegalStateException(Throwable cause) {
		super(cause);
	}

}
