package org.rifidi.edge.core.communication.simple.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.communication.service.ConnectionEventListener;
import org.rifidi.edge.core.communication.service.ConnectionListener;
import org.rifidi.edge.core.communication.service.ConnectionService;
import org.rifidi.edge.core.communication.simple.impl.SimpleConnectionImpl;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;

public class ConnectionServiceImpl implements ConnectionService {

	private List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();
	private List<Connection> connections = new ArrayList<Connection>();

	@Override
	public Connection createConnection(ConnectionManager connectionManager,
			ReaderInfo readerInfo, ConnectionEventListener listener) throws RifidiConnectionException {
		SimpleConnectionImpl connection = new SimpleConnectionImpl(connectionManager, listener);
		fireAddEvent(connection);
		return connection;
	}

	@Override
	public void destroyConnection(Connection connection, ConnectionEventListener listener) {
		((SimpleConnectionImpl) connection).disconnect();
		fireRemoveEvent(connection);
	}

	@Override
	public List<Connection> getAllConnections() {
		return new ArrayList<Connection>(connections);
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
