package org.rifidi.edge.core.readerplugin.connectionmanager;

import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.core.communication.service.ConnectionEventListener;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

public abstract class ConnectionManager implements ConnectionExceptionListener {

	protected Set<ConnectionEventListener> listeners = new HashSet<ConnectionEventListener>();
	
	
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

	public void addConnectionEventListener(
			ConnectionEventListener listener){
		listeners.add(listener);
	}

	public void removeConnectionEventListener(
			ConnectionEventListener listener){
		listeners.remove(listener);
	}
	
	protected void fireConnectEvent(){
		for (ConnectionEventListener listener: listeners){
			listener.connected();
		}
	}
	
	protected void fireDisconnectEvent(){
		for (ConnectionEventListener listener: listeners){
			listener.disconnected();
		}
	}
}
