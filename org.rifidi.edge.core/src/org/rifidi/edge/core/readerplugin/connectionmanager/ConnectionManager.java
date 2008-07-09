package org.rifidi.edge.core.readerplugin.connectionmanager;


import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

public abstract class ConnectionManager {

	
	
	
	 public ConnectionManager(ReaderInfo readerInfo) {
	 }

	//TODO Rename this.
	public abstract ConnectionStreams createCommunication()
			throws RifidiConnectionException;

	//TODO Rename this to authenticate
	public abstract void connect()
			throws RifidiConnectionException;

	public abstract void disconnect();

	public abstract int getMaxNumConnectionsAttemps();

	public abstract long getReconnectionIntervall();

	public abstract CommunicationProtocol getCommunicationProtocol();

	public abstract void startKeepAlive();

	public abstract void stopKeepAlive();



}
