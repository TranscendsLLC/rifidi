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
import org.rifidi.edge.core.communication.ICommunicationConnection;
import org.rifidi.edge.core.communication.Protocol;
import org.rifidi.edge.core.communication.buffer.Communication;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;


public class CommunicationServiceImpl implements CommunicationService {
	private static final Log logger = LogFactory.getLog(CommunicationServiceImpl.class);
	

	Map<ICommunicationConnection, Communication> communications;

	public CommunicationServiceImpl(){
		//logger.debug();
		communications = new HashMap<ICommunicationConnection, Communication>();
	}
	
	@Override
	public ICommunicationConnection createConnection(IReaderPlugin plugin,
			Protocol protocol) throws UnknownHostException, ConnectException, IOException {
		logger.debug("Connecting: " + plugin.getReaderInfo().getIPAddress() + ":" + plugin.getReaderInfo().getPort() + " ...");
		Socket socket = new Socket(plugin.getReaderInfo().getIPAddress(), plugin.getReaderInfo().getPort());
		
		Communication communication = new Communication(socket, protocol);
		
		ICommunicationConnection communicationConnection = communication.startCommunication();
		
		communications.put(communicationConnection, communication);

		return communicationConnection;
	}

	@Override
	public void destroyConnection(ICommunicationConnection connection) throws IOException {
		
		Communication communication = communications.get(connection);
		communications.remove(connection);
		
		logger.debug("Disconnecting: " + communication.getSocket().getInetAddress() + ":" 
				+ communication.getSocket().getPort() + " ...");
		
		communication.getSocket().close();
		communication.stopCommunication();
	}

	

}
