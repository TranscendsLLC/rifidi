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
package org.rifidi.edge.core.communication;

import java.util.List;

/**
 * @author Matthew Dean - matt@pramari.com
 *
 */
public abstract class Protocol {
	/**
	 * 
	 * @param arg
	 * @return
	 */
	public abstract List<Object> toObject(byte[] arg);
	
	/**
	 * 
	 * @param arg
	 * @return
	 */
	public abstract byte[] fromObject(Object arg);
}
