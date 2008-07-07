package org.rifidi.edge.readerplugin.dummy;


import org.rifidi.edge.core.communication.service.ConnectionEventListener;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionStreams;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

public class DummyConnectionManager extends ConnectionManager {

	public DummyConnectionManager(ReaderInfo readerInfo) {
		super(readerInfo);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void connect() {
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
	public void startKeepAlive() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addConnectionEventListener(ConnectionEventListener event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ConnectionStreams createCommunication()
			throws RifidiConnectionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaxNumConnectionsAttemps() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getReconnectionIntervall() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeConnectionEventListener(ConnectionEventListener event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopKeepAlive() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionExceptionEvent(Exception exception) {
		// TODO Auto-generated method stub
		
	}

	
}
