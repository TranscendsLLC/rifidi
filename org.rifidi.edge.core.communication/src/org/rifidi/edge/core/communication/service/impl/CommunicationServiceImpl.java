package org.rifidi.edge.core.communication.service.impl;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.buffer.ConnectionBuffer;
import org.rifidi.edge.core.communication.handler.Communication;
import org.rifidi.edge.core.communication.handler.impl.AsynchronousCommunication;
import org.rifidi.edge.core.communication.handler.impl.SynchronousCommunication;
import org.rifidi.edge.core.communication.protocol.Protocol;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;

/**
 * @author jerry
 * 
 */
public class CommunicationServiceImpl implements CommunicationService {
	private static final Log logger = LogFactory
			.getLog(CommunicationServiceImpl.class);

	Map<ConnectionBuffer, Communication> communications;

	/**
	 * Default Constructor
	 */
	public CommunicationServiceImpl() {
		// logger.debug();
		communications = new HashMap<ConnectionBuffer, Communication>();
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
		if ((plugin == null) || (info == null) || (protocol == null))
			throw new NullPointerException(
					"No null pointers allowed in arguments to this method.");

		logger.debug("Connecting: " + info.getIPAddress() + ":"
				+ info.getPort() + " ...");
		Socket socket = new Socket(info.getIPAddress(), info.getPort());

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
