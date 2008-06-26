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

import org.rifidi.edge.core.communication.buffer.ConnectionBuffer;
import org.rifidi.edge.core.communication.protocol.Protocol;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;

 /**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public interface CommunicationService {

	/**
	 * This method takes the required information and connects to the reader and
	 * returns an asynchronous buffer for the plugin to use.
	 * 
	 * @param plugin
	 *            The Plugin itself
	 * @param info
	 *            Plugin specific information about the reader.
	 * @param protocol
	 *            Plugin specific protocol implementation.
	 * @return A communication buffer object.
	 * @throws UnknownHostException
	 * @throws ConnectException
	 * @throws IOException
	 * @throws NullPointerException
	 *             Throws this if plugin, info, or protocol are null.
	 */
	public ConnectionBuffer createConnection(IReaderPlugin plugin,
			AbstractReaderInfo info, Protocol protocol)
			throws UnknownHostException, ConnectException, IOException;

	/**
	 * Destroys the connection represented by the buffer buffer sent in.
	 * 
	 * @param connection
	 * @throws IOException
	 */
	public void destroyConnection(ConnectionBuffer buffer) throws IOException;
}
