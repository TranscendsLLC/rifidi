package org.rifidi.edge.core.readerplugin.connectionmanager;


import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

public abstract class ConnectionManager {

	ReaderInfo info;
	
	
	public ConnectionManager(ReaderInfo readerInfo) {
		info = readerInfo;
	}

	//TODO Rename this.
	public abstract ConnectionStreams createCommunication()
			throws RifidiConnectionException;

	//TODO Rename this to authenticate
	public abstract void connect(Connection connection)
			throws RifidiConnectionException;

	public abstract void disconnect(Connection connection);

	public abstract int getMaxNumConnectionsAttemps();

	public abstract long getReconnectionIntervall();

	public abstract CommunicationProtocol getCommunicationProtocol();

	public abstract void startKeepAlive();

	public abstract void stopKeepAlive();

	public String toString(){
		return this.getClass().getSimpleName() + ": " + info.getIpAddress() + ":" + info.getPort();
	}

}
