/*
 *  ConnectionBuffer.java
 *
 *  Created:	Jun 27, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.service.communication.buffer;

import java.io.IOException;

import org.rifidi.edge.core.exception.readerConnection.RifidiIllegalOperationException;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public interface ConnectionBuffer {

	/**
	 * @param message
	 */
	public void send(Object message) throws RifidiIllegalOperationException,
			IOException;

	/**
	 * @return
	 */
	public Object recieve() throws RifidiIllegalOperationException, IOException;

	/**
	 * @param message
	 * @return
	 */
	public Object sendAndRecieve(Object message)
			throws RifidiIllegalOperationException, IOException;

}
