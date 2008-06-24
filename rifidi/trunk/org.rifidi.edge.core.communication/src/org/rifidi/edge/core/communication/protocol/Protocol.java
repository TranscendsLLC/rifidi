/* 
 * Protocol.java
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.communication.protocol;

import java.util.List;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public abstract class Protocol {
	
	/**
	 * This method receives a raw array of bytes from the socket and
	 * should transform it into a reader specific object message that
	 * its adapter can understand. 
	 * @param arg
	 * @return
	 */
	public abstract List<Object> toObject(byte[] arg);

	/**
	 * This method receives a reader specific object and
	 * should transform it into an array of bytes that
	 * the specific reader can understand
	 * @param arg
	 * @return
	 */
	public abstract byte[] fromObject(Object arg);
}
