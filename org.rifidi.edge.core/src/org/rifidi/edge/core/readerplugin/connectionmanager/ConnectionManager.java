package org.rifidi.edge.core.readerplugin.connectionmanager;

import org.rifidi.edge.core.communication.service.ConnectionEventListener;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

public abstract class ConnectionManager implements ConnectionExceptionListener {

	public ConnectionManager(ReaderInfo readerInfo) {
	}

	public abstract ConnectionStreams createCommunication()
			throws RifidiConnectionException;

	public abstract void connect() throws RifidiConnectionException;

	public abstract void disconnect();

	public abstract int getMaxNumConnectionsAttemps();

	public abstract long getReconnectionIntervall();

	public abstract CommunicationProtocol getCommunicationProtocol();

	public abstract void startKeepAlive();

	public abstract void stopKeepAlive();
	
	public abstract void addConnectionEventListener(ConnectionEventListener event);
	
	public abstract void removeConnectionEventListener(ConnectionEventListener event);
}
