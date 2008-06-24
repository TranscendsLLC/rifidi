package org.rifidi.edge.core.communication.service;


import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.CommunicationService;
import org.rifidi.edge.core.communication.CommunicationBuffer;
import org.rifidi.edge.core.communication.Protocol;
import org.rifidi.edge.core.communication.buffer.Communication;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;


/**
 * @author jerry
 *
 */
public class CommunicationServiceImpl implements CommunicationService {
	private static final Log logger = LogFactory.getLog(CommunicationServiceImpl.class);
	

	Map<CommunicationBuffer, Communication> communications;

	/**
	 * Default Constructor
	 */
	public CommunicationServiceImpl(){
		//logger.debug();
		communications = new HashMap<CommunicationBuffer, Communication>();
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.CommunicationService#createConnection(org.rifidi.edge.core.readerPlugin.IReaderPlugin, org.rifidi.edge.core.readerPlugin.AbstractReaderInfo, org.rifidi.edge.core.communication.Protocol)
	 */
	@Override
	public CommunicationBuffer createConnection(IReaderPlugin plugin, AbstractReaderInfo info,
			Protocol protocol) throws UnknownHostException, ConnectException, IOException {
		logger.debug("Connecting: " + info.getIPAddress() + ":" + info.getPort() + " ...");
		Socket socket = new Socket(info.getIPAddress(), info.getPort());
		
		Communication communication = new Communication(socket, protocol);
		
		CommunicationBuffer communicationConnection = communication.startCommunication();
		
		communications.put(communicationConnection, communication);

		return communicationConnection;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.CommunicationService#destroyConnection(org.rifidi.edge.core.communication.ICommunicationConnection)
	 */
	@Override
	public void destroyConnection(CommunicationBuffer connection) throws IOException {

		Communication communication = communications.get(connection);
		communications.remove(connection);
		
		communication.stopCommunication();
		
	}

}
