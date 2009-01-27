package org.rifidi.edge.core.communication.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.rifidi.edge.core.api.communication.Connection;
import org.rifidi.edge.core.api.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.communication.impl.Communication;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;

/**
 * Implemtation of the ConnectionService. This Service allows to create and
 * destroy connections to readers. It also keeps track of current connections.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class ConnectionServiceImpl implements ConnectionService {

	private List<ConnectionServiceListener> listeners = new ArrayList<ConnectionServiceListener>();
	private HashMap<Connection, Communication> connections = new HashMap<Connection, Communication>();

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.service.ConnectionService#createConnection(org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager, org.rifidi.edge.core.readerplugin.ReaderInfo, org.rifidi.edge.core.communication.service.CommunicationStateListener)
	 */
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

		connections.put(connection, communication);
		fireAddEvent(connection);

		return connection;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.service.ConnectionService#destroyConnection(org.rifidi.edge.core.communication.Connection, org.rifidi.edge.core.communication.service.CommunicationStateListener)
	 */
	@Override
	public void destroyConnection(Connection connection,
			CommunicationStateListener listener) {
		Communication communication = connections.remove(connection);
		communication.removeCommunicationStateListener(listener);
		communication.disconnect();
		fireRemoveEvent(connection);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.service.ConnectionService#getAllConnections()
	 */
	@Override
	public List<Connection> getAllConnections() {
		return new ArrayList<Connection>(connections.keySet());
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.service.ConnectionService#addConnectionServiceListener(org.rifidi.edge.core.communication.service.ConnectionServiceListener)
	 */
	@Override
	public void addConnectionServiceListener(ConnectionServiceListener listener) {
		listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.service.ConnectionService#removeConnectionServiceListener(org.rifidi.edge.core.communication.service.ConnectionServiceListener)
	 */
	@Override
	public void removeConnectionServiceListener(
			ConnectionServiceListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Fire new connection created event
	 * 
	 * @param event the connection created 
	 */
	private void fireAddEvent(Connection event) {
		for (ConnectionServiceListener listener : listeners) {
			listener.addEvent(event);
		}
	}

	/**
	 * Fire connection deleted event
	 * 
	 * @param event the connection deleted
	 */
	private void fireRemoveEvent(Connection event) {
		for (ConnectionServiceListener listener : listeners) {
			listener.removeEvent(event);
		}
	}
}
