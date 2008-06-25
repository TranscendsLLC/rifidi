/*
 *  RifidiReaderPluginException.java
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

import org.rifidi.edge.core.exception.RifidiException;


/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class RifidiReaderPluginException extends RifidiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2946763040951318913L;

	public RifidiReaderPluginException() {

	}

	public RifidiReaderPluginException(String message) {
		super(message);
	}

	public RifidiReaderPluginException(Throwable cause) {
		super(cause);
	}

	public RifidiReaderPluginException(String message, Throwable cause) {
		super(message, cause);
	}

}
