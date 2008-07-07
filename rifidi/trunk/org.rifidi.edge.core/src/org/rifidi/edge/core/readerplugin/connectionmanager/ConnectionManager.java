package org.rifidi.edge.core.readerplugin.connectionmanager;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

public interface ConnectionManager {

	public void connect(ReaderInfo readerInfo, Connection connection);
	
	public void reconnect();

	public void disconnect();
	
	public int getMaxConnectionAttemps();
	
	public long getConnectionAttemptInterval();

	public CommunicationProtocol getCommunicationProtocol();

	public void startKeepAlive();
}
