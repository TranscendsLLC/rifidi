/**
 * 
 */
package org.rifidi.edge.readerplugin.llrp.plugin;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.llrp.ltk.generated.enumerations.ConnectionAttemptStatusType;
import org.llrp.ltk.generated.enumerations.StatusCode;
import org.llrp.ltk.generated.messages.CLOSE_CONNECTION;
import org.llrp.ltk.generated.messages.CLOSE_CONNECTION_RESPONSE;
import org.llrp.ltk.generated.messages.READER_EVENT_NOTIFICATION;
import org.llrp.ltk.generated.parameters.ConnectionAttemptEvent;
import org.llrp.ltk.generated.parameters.LLRPStatus;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionStreams;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;
import org.rifidi.edge.readerplugin.llrp.protocol.LLRPCommunicationProtocol;

/**
 * This is the implementation for the LLRPConnectonManager. It assumes a
 * client-initiated connection. The LLRP reader should be listening for LLRP
 * connections at the given IP and port
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class LLRPConnectionManager extends ConnectionManager {

	/**
	 * The reader info for this class
	 */
	private ReaderInfo readerInfo;

	/**
	 * The socket for this connection
	 */
	private Socket socket;

	/**
	 * The communication protocol for the LLRP reader
	 */
	private LLRPCommunicationProtocol communicationProtocol;

	private Log logger = LogFactory.getLog(LLRPCommunicationProtocol.class);

	public LLRPConnectionManager(ReaderInfo readerInfo) {
		super(readerInfo);
		this.readerInfo = readerInfo;
		communicationProtocol = new LLRPCommunicationProtocol();
	}

	@Override
	public void connect(Connection connection) throws RifidiConnectionException {
		try {

			Object o= connection.receiveMessage(1000);
			if(o==null){
				throw new RifidiConnectionException("CannotConnect");
			}
			READER_EVENT_NOTIFICATION event = (READER_EVENT_NOTIFICATION) o;
			ConnectionAttemptEvent connAttempt = event
					.getReaderEventNotificationData()
					.getConnectionAttemptEvent();
			if (connAttempt == null) {
				throw new RifidiConnectionException(
						"Connection failed because the READER_EVENT_NOTIFICATION message did not contain a ConnectionAttemptEvent parameter");
			}
			ConnectionAttemptStatusType statusType = connAttempt.getStatus();
			int status = statusType.intValue();
			// status must be 0 for a valid connection
			if (status == ConnectionAttemptStatusType.Failed_A_Reader_Initiated_Connection_Already_Exists) {
				throw new RifidiConnectionException(
						"Connection failed because a Reader initiated connection already exists");
			} else if (status == ConnectionAttemptStatusType.Failed_A_Client_Initiated_Connection_Already_Exists) {
				throw new RifidiConnectionException(
						"Connection failed because a Client initiated connection already exists");
			} else if (status == ConnectionAttemptStatusType.Failed_Reason_Other_Than_A_Connection_Already_Exists) {
				throw new RifidiConnectionException(
						"Connection failed for a reason other than a connection already existing");
			} else if (status == ConnectionAttemptStatusType.Another_Connection_Attempted) {
				throw new RifidiConnectionException(
						"Connection failed because another connection was attempted");
			} else if (status != 0) {
				throw new RifidiConnectionException("Connection failed");
			}

		} catch (IOException e) {
			throw new RifidiConnectionException(
					"Connection failed when reading from the socket", e);
		} catch (ClassCastException ex) {
			throw new RifidiConnectionException(
					"Connection failed because an incorrect message was recieved",
					ex);
		}
		logger.debug("LLRP Reader made a valid logical connection");
	}

	@Override
	public ConnectionStreams createCommunication()
			throws RifidiConnectionException {
		ConnectionStreams streams = null;
		try {
			socket = new Socket(readerInfo.getIpAddress(), readerInfo.getPort());
			streams = new ConnectionStreams(socket.getInputStream(), socket
					.getOutputStream());
		} catch (UnknownHostException e) {
			throw new RifidiConnectionException("Cannot create socket: "
					+ readerInfo.getIpAddress() + ":" + readerInfo.getPort()
					+ " is an unknown host", e);
		} catch (IOException e) {
			throw new RifidiConnectionException(
					"Cannot create socket due to IOException ", e);
		}
		return streams;
	}

	@Override
	public void disconnect(Connection connection) {
		CLOSE_CONNECTION closeConnection = new CLOSE_CONNECTION();	
		try {
			connection.sendMessage(closeConnection);
			Object o = connection.receiveMessage(1000);
			if(o == null){
				throw new IOException();
			}
			CLOSE_CONNECTION_RESPONSE response = (CLOSE_CONNECTION_RESPONSE) connection
					.receiveMessage();
			LLRPStatus status = response.getLLRPStatus();
			StatusCode statuscode = status.getStatusCode();
			int statusInt = statuscode.toInteger();
			if (statusInt == 0) {
				// log that everything is peachy. Reader will close socket
				logger.debug("disconnect status is successful");
			}

		} catch (IOException e) {
			// log error
			logger.error("IOException while trying to close"
					+ " connection, closing connection manually");
		} catch (ClassCastException e) {
			logger.error("CastClassException while trying to "
					+ "close connection, closing connection manually");
		}

		// make sure socket is closed
		try {
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			// socket was already closed
			logger.debug("Socket was already closed.");
		}

	}

	@Override
	public CommunicationProtocol getCommunicationProtocol() {
		return this.communicationProtocol;
	}

	@Override
	public int getMaxNumConnectionsAttempts() {
		if (this.readerInfo.getMaxNumConnectionsAttempts() > 1) {
			return readerInfo.getMaxNumConnectionsAttempts();
		} else {
			return 1;
		}
	}

	@Override
	public long getReconnectionInterval() {
		if (this.readerInfo.getReconnectionInterval() > 0) {
			return readerInfo.getReconnectionInterval();
		} else {
			return 0;
		}
	}

	@Override
	public void startKeepAlive(Connection connection) {
		// TODO We could make the reader send us keepalives, but would it be
		// useful?

	}

	@Override
	public void stopKeepAlive(Connection connection) {
	}

}
