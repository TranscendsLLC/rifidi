/*
 *  CommunicationServiceImpl.java
 *
 *  Created:	Jun 27, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.communication.service.impl;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.handler.Communication;
import org.rifidi.edge.core.communication.handler.impl.AsynchronousCommunication;
import org.rifidi.edge.core.communication.handler.impl.SynchronousCommunication;
import org.rifidi.edge.core.communication.protocol.Protocol;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.service.communication.CommunicationService;
import org.rifidi.edge.core.service.communication.buffer.ConnectionBuffer;


/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class CommunicationServiceImpl implements CommunicationService {
	private static final Log logger = LogFactory
			.getLog(CommunicationServiceImpl.class);

	private Map<ConnectionBuffer, Communication> communications;

	/**
	 * Default Constructor
	 */
	public CommunicationServiceImpl() {
		
		//TODO: Look if this is needed latter.
		communications = Collections.synchronizedMap(new HashMap<ConnectionBuffer, Communication>());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.CommunicationService#createConnection(org.rifidi.edge.core.readerPlugin.IReaderPlugin,
	 *      org.rifidi.edge.core.readerPlugin.AbstractReaderInfo,
	 *      org.rifidi.edge.core.communication.Protocol)
	 */
	@Override
	public ConnectionBuffer createConnection(IReaderPlugin plugin,
			AbstractReaderInfo info, Protocol protocol)
			throws UnknownHostException, ConnectException, IOException {
		if ((plugin == null) || (info == null) ||
			(protocol == null) || (info.getCommunicationType() == null) ||
			(info.getIPAddress() == null) || (info.getReaderType() == null) || 
			(info.getProtocol() == null))
			throw new NullPointerException(
					"No null pointers allowed in arguments to this method" + 
					" nor in any AbstractReaderInfo object.");

		logger.debug("Connecting: " + info.getIPAddress() + ":"
				+ info.getPort() + " ...");
		
		//TODO: Think about setting a timeout for this socket.
		Socket socket = new Socket(info.getIPAddress(), info.getPort());
		//socket.setSoTimeout(500);

		Communication communication = null;
		switch (info.getCommunicationType()) {
		case SYNCHRONOUS:
			communication = new SynchronousCommunication(socket, protocol);
			break;
		case ASYNCHRONOUS:
			communication = new AsynchronousCommunication(socket, protocol);
			break;
		default:
			throw new ConnectException("No CommunicationType selected");
		}

		ConnectionBuffer communicationConnection = communication
				.startCommunication();

		communications.put(communicationConnection, communication);

		return communicationConnection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.CommunicationService#destroyConnection(org.rifidi.edge.core.communication.ICommunicationConnection)
	 */
	@Override
	public void destroyConnection(ConnectionBuffer buffer) throws IOException {

		Communication communication = communications.remove(buffer);
		communication.stopCommunication();

	}

}
