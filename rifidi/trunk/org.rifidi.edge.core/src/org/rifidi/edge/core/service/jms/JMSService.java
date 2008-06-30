/*
 *  JMSService.java
 *
 *  Created:	Jun 27, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.service.jms;

import org.rifidi.edge.core.connection.IReaderConnection;



/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public interface JMSService {
	
	/**
	 * Registers an IReaderConnection with this service
	 * @param connection
	 * @return True if successful. False if unsuccessful.
	 */
	public boolean register(IReaderConnection connection);
	
	/**
	 * Unregisters an IReaderConnection with this service.
	 * @param connection
	 */
	public void unregister(IReaderConnection connection);
}
