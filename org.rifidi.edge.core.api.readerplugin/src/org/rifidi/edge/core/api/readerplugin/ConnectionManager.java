package org.rifidi.edge.core.api.readerplugin;


import org.rifidi.edge.core.api.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.api.readerplugin.communication.CommunicationProtocol;
import org.rifidi.edge.core.api.readerplugin.communication.Connection;
import org.rifidi.edge.core.api.readerplugin.communication.ConnectionStreams;

/**
 * The ConnectionManager is used for establishing a physical and logical
 * connection to a certain reader. This Interface provides the minimal set of
 * functions needed to establish a connection and in the case of a loss to
 * reestablish it.
 * 
 * CreateCommunication - establish the physical connection (mostly Socket)
 * Connect - establish logical connection (Authentication)
 * 
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public abstract class ConnectionManager {

	/**
	 * Create a new ConnectionManager
	 */
	public ConnectionManager() {

	}

	// TODO Rename this.
	/**
	 * Create the physical connection layer. In most cases this will create a
	 * socket, connect to it, and return the connection streams.
	 * 
	 * @return the input and output stream of the physical connection object
	 * @throws RifidiConnectionException
	 *             if the physical connection could not be established
	 */
	public abstract ConnectionStreams createCommunication()
			throws RifidiConnectionException;

	// TODO Rename this to authenticate
	/**
	 * Create the logical connection layer. In most cases this will authenticate
	 * or send the initial connection messages to the newly created reader
	 * connection.
	 * 
	 * @param connection
	 *            the connection buffer to the reader
	 * @throws RifidiConnectionException
	 */
	public abstract void connect(Connection connection)
			throws RifidiConnectionException;

	/**
	 * Disconnect the connection to the reader and close all previously opened
	 * handles(like Sockets). In some cases this will send the quit command
	 * message to the reader and wait for the Socket to close.
	 * 
	 * @param connection
	 *            the connection buffer to the reader
	 */
	public abstract void disconnect(Connection connection);

	/**
	 * Get the maximal attempts to connect if the connection failed
	 * 
	 * @return number of connection attempts if connection fails
	 */
	public abstract int getMaxNumConnectionsAttempts();

	/**
	 * Get the wait time interval before trying to establish a new connection.
	 * 
	 * @return time to wait in ms
	 */
	public abstract long getReconnectionInterval();

	/**
	 * Get the CommunicationProtocol of this ReaderPlugin.
	 * 
	 * @return the reader specific communication protocol
	 */
	public abstract CommunicationProtocol getCommunicationProtocol();

	/**
	 * Start sending keep alives to the reader to avoid a connection loss
	 * 
	 * @param connection
	 */
	public abstract void startKeepAlive(Connection connection);

	/**
	 * Stop sending keep alives
	 * 
	 * @param connection
	 */
	public abstract void stopKeepAlive(Connection connection);
}
