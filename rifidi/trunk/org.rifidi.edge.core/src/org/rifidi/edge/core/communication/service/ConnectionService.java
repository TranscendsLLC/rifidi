package org.rifidi.edge.core.communication.service;

import java.util.List;

import org.rifidi.edge.core.api.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.api.readerplugin.ConnectionManager;
import org.rifidi.edge.core.api.readerplugin.ReaderInfo;
import org.rifidi.edge.core.api.readerplugin.communication.Connection;
/**
 * ConnectionService to create and delete connections to readers
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface ConnectionService {

	/**
	 * Create a new Connection to the specified Reader
	 * 
	 * @param connectionManager
	 *            ConnectionManager of the specified ReaderPlugin
	 * @param readerInfo
	 *            ReaderInfo describing the specified ReaderPlugin and it's
	 *            connection properties
	 * @param communicationStateListener
	 *            listener for changes on the communication like connected,
	 *            disconnected and error
	 * @return the connection to send and receive messages on
	 * @throws RifidiConnectionException
	 *             if the connection could not be established
	 */
	public Connection createConnection(ConnectionManager connectionManager,
			ReaderInfo readerInfo,
			CommunicationStateListener communicationStateListener)
			throws RifidiConnectionException;

	/**
	 * Destroy and Disconnect a previously created connection
	 * 
	 * @param connection
	 *            the connection to release
	 * @param listener
	 *            the listener to release from the connection
	 */
	// TODO: do we really need to passin the communicationStateListener or can
	// we just remove all listeners since we are about to destroy the connection
	// anyways
	public void destroyConnection(Connection connection,
			CommunicationStateListener listener);

	/**
	 * Get a list of currently used connections
	 * 
	 * @return a list of connections
	 */
	public List<Connection> getAllConnections();

	/**
	 * Add a listener to this service. It will be notified if there is a new
	 * connection or if there was a connection destroyed
	 * 
	 * @param listener
	 *            the listener for connectionservice events
	 */
	public void addConnectionServiceListener(ConnectionServiceListener listener);

	/**
	 * Remove a previously added listeners
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeConnectionServiceListener(
			ConnectionServiceListener listener);
}
