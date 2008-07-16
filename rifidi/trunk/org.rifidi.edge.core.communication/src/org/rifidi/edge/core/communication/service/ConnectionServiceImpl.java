package org.rifidi.edge.core.communication.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.communication.impl.Communication;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;

public class ConnectionServiceImpl implements ConnectionService {

	private List<ConnectionServiceListener> listeners = new ArrayList<ConnectionServiceListener>();
	private HashMap<Connection, Communication> connections = new HashMap<Connection, Communication>();

	@Override
	public Connection createConnection(ConnectionManager connectionManager,
			ReaderInfo readerInfo,
			CommunicationStateListener communicationStateListener)
			throws RifidiConnectionException {
		Connection connection = null;
		Communication communication = new Communication(connectionManager);
		communication.addCommunicationStateListener(communicationStateListener);
		connection = communication.connect();

		if (connection == null)
			throw new RifidiConnectionException("Connection returned null."
					+ " This means there is a severe error in "
					+ connectionManager.getClass().getName());

		fireAddEvent(connection);
		connections.put(connection, communication);
		return connection;
	}

	@Override
	public void destroyConnection(Connection connection,
			CommunicationStateListener listener) {
		Communication communication = connections.remove(connection);
		communication.removeCommunicationStateListener(listener);
		communication.disconnect();
		fireRemoveEvent(connection);
	}

	@Override
	public List<Connection> getAllConnections() {
		return new ArrayList<Connection>(connections.keySet());
	}

	@Override
	public void addConnectionServiceListener(ConnectionServiceListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeConnectionServiceListener(
			ConnectionServiceListener listener) {
		listeners.remove(listener);
	}

	private void fireAddEvent(Connection event) {
		for (ConnectionServiceListener listener : listeners) {
			listener.addEvent(event);
		}
	}

	private void fireRemoveEvent(Connection event) {
		for (ConnectionServiceListener listener : listeners) {
			listener.removeEvent(event);
		}
	}
}
