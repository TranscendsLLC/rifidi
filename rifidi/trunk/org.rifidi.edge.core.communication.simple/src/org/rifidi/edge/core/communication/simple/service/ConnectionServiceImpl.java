package org.rifidi.edge.core.communication.simple.service;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.communication.service.CommunicationStateListener;
import org.rifidi.edge.core.communication.service.ConnectionService;
import org.rifidi.edge.core.communication.service.ConnectionServiceListener;
import org.rifidi.edge.core.communication.simple.impl.SimpleConnectionImpl;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;

public class ConnectionServiceImpl implements ConnectionService {

	private List<ConnectionServiceListener> listeners = new ArrayList<ConnectionServiceListener>();
	private List<Connection> connections = new ArrayList<Connection>();

	@Override
	public Connection createConnection(ConnectionManager connectionManager,
			ReaderInfo readerInfo, CommunicationStateListener listener) throws RifidiConnectionException {
		SimpleConnectionImpl connection = new SimpleConnectionImpl(connectionManager, listener);
		fireAddEvent(connection);
		return connection;
	}

	@Override
	public void destroyConnection(Connection connection, CommunicationStateListener listener) {
		((SimpleConnectionImpl) connection).disconnect();
		fireRemoveEvent(connection);
	}

	@Override
	public List<Connection> getAllConnections() {
		return new ArrayList<Connection>(connections);
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

	@Override
	public void addConnectionServiceListener(ConnectionServiceListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeConnectionServiceListener(
			ConnectionServiceListener listener) {
		listeners.remove(listener);
	}
}
