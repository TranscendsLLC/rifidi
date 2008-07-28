package org.rifidi.edge.readerplugin.dummyenhanced.plugin;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionStreams;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;
import org.rifidi.edge.readerplugin.dummyenhanced.protocol.DummyCommunicationProtocol;

public class DummyEnhancedConnectionManager extends ConnectionManager {

	private static final Log logger = LogFactory
			.getLog(DummyEnhancedConnectionManager.class);
	DummyEnhancedReaderInfo info;

	/* used only when the dummy adapter is set to random errors */
	Random random;

	/**
	 * The socket for this connection
	 */
	private Socket socket;

	public DummyEnhancedConnectionManager(ReaderInfo readerInfo) {
		super(readerInfo);
		this.info = (DummyEnhancedReaderInfo) readerInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager
	 * #connect()
	 */
	@Override
	public void connect(Connection connection) throws RifidiConnectionException {
		// always succeeds
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager
	 * #disconnect()
	 */
	@Override
	public void disconnect(Connection connection) {

		//logical disconnect
		try {
			connection.sendMessage(new String("quit"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//physical disconnect
		try {
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			logger.debug("Socket already closed");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager
	 * #getCommunicationProtocol()
	 */
	@Override
	public CommunicationProtocol getCommunicationProtocol() {
		return new DummyCommunicationProtocol();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager
	 * #startKeepAlive()
	 */
	@Override
	public void startKeepAlive(Connection connection) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager
	 * #createCommunication()
	 */
	@Override
	public ConnectionStreams createCommunication()
			throws RifidiConnectionException {


		ConnectionStreams streams = null;
		try {
			socket = new Socket(info.getIpAddress(), info.getPort());
			streams = new ConnectionStreams(socket.getInputStream(), socket
					.getOutputStream());
		} catch (UnknownHostException e) {
			throw new RifidiConnectionException("Cannot create socket: "
					+ info.getIpAddress() + ":" + info.getPort()
					+ " is an unknown host", e);
		} catch (IOException e) {
			throw new RifidiConnectionException(
					"Cannot create socket due to IOException ", e);
		}
		return streams;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager
	 * #getMaxNumConnectionsAttemps()
	 */
	@Override
	public int getMaxNumConnectionsAttemps() {
		return (info.getMaxNumConnectionsAttemps() >= 1) ? info
				.getMaxNumConnectionsAttemps() : 3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager
	 * #getReconnectionIntervall()
	 */
	@Override
	public long getReconnectionIntervall() {
		return (info.getReconnectionIntervall() >= 0) ? info
				.getReconnectionIntervall() : 1000;
	}

	@Override
	public void stopKeepAlive(Connection connection) {
		// ignore this.

	}

}
