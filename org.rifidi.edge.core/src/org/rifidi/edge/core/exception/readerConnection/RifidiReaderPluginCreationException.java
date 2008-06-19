/*
 *  RifidiReaderPluginCreationException.java
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

public class RifidiReaderPluginCreationException extends RifidiReaderPluginException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3340443416695449971L;

	public RifidiReaderPluginCreationException() {
		super();
	}

	public RifidiReaderPluginCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public RifidiReaderPluginCreationException(String message) {
		super(message);
	}

	public RifidiReaderPluginCreationException(Throwable cause) {
		super(cause);
	}

}
