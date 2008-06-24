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


import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import org.rifidi.edge.core.communication.CommunicationBuffer;
import org.rifidi.edge.core.communication.protocol.Protocol;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;

/**
 * @author jerry
 *
 */
public interface CommunicationService {
	
	/**
	 * @param plugin
	 * @param info
	 * @param protocol
	 * @return
	 * @throws UnknownHostException
	 * @throws ConnectException
	 * @throws IOException
	 */
	public CommunicationBuffer createConnection(IReaderPlugin plugin, AbstractReaderInfo info, Protocol protocol) throws UnknownHostException, ConnectException, IOException;
	
	/**
	 * @param connection
	 * @throws IOException
	 */
	public void destroyConnection(CommunicationBuffer connection)
			throws IOException;
}
