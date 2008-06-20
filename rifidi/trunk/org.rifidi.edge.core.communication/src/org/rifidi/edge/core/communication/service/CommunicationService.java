/* 
 * CommunicationService.java
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.communication.service;

import org.rifidi.edge.core.communication.buffer.Buffer;
import org.rifidi.edge.core.communication.buffer.Protocol;

/**
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public interface CommunicationService {
	public Buffer register(Protocol protocol);
}
