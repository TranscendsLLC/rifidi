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

	private List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();
	private HashMap<Connection, Communication> connections = new HashMap<Connection, Communication>();

	@Override
	public Connection createConnection(ConnectionManager connectionManager,
			ReaderInfo readerInfo, ConnectionEventListener listener) throws RifidiConnectionException {
		Connection connection = null;
		Communication communication = new Communication(connectionManager);
		connection = communication.connect();
		fireAddEvent(connection);
		communication.addConnectionEventListener(listener);
		return connection;
	}

	@Override
	public void destroyConnection(Connection connection, ConnectionEventListener listener) {
		Communication communication = connections.remove(connection);
		communication.removeConnectionEventListener(listener);
		communication.disconnect();
		fireRemoveEvent(connection);
	}

	@Override
	public List<Connection> getAllConnections() {
		return new ArrayList<Connection>(connections.keySet());
	}

	@Override
	public void addConnectionListener(ConnectionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeConnectionListener(ConnectionListener listener) {
		listeners.remove(listener);
	}

	private void fireAddEvent(Connection event) {
		for (ConnectionListener listener : listeners) {
			listener.addEvent(event);
		}
	}

	private void fireRemoveEvent(Connection event) {
		for (ConnectionListener listener : listeners) {
			listener.removeEvent(event);
		}
	}
}
