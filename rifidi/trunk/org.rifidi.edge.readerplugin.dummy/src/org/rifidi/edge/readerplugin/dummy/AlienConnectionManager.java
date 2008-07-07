package org.rifidi.edge.readerplugin.dummy;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

public class AlienConnectionManager implements ConnectionManager {

	@Override
	public void connect(ReaderInfo readerInfo, Connection connection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CommunicationProtocol getCommunicationProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getConnectionAttemptInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxConnectionAttemps() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void reconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startKeepAlive() {
		// TODO Auto-generated method stub
		
	}

	
}
