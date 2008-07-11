package org.rifidi.edge.core.communication.service;

import java.util.List;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;

public interface ConnectionService {

	public Connection createConnection(ConnectionManager connectionManager,
			ReaderInfo readerInfo, CommunicationStateListener communicationStateListener)
		throws RifidiConnectionException;

	public void destroyConnection(Connection connection, CommunicationStateListener listener);

	public List<Connection> getAllConnections();

	public void addConnectionServiceListener(ConnectionServiceListener listener);

	public void removeConnectionServiceListener(ConnectionServiceListener listener);
}
