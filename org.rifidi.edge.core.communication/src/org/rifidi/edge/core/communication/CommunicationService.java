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
package org.rifidi.edge.core.communication;


import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import org.rifidi.edge.core.communication.ICommunicationConnection;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;

public interface CommunicationService {
	public ICommunicationConnection createConnection(IReaderPlugin plugin, AbstractReaderInfo info, Protocol protocol) throws UnknownHostException, ConnectException, IOException;
	public void destroyConnection(ICommunicationConnection connection)
			throws IOException;
}
