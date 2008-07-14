/**
 * 
 */
package org.rifidi.edge.readerplugin.dummy.plugin;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionStreams;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;
import org.rifidi.edge.readerplugin.dummy.protocol.DummyCommunicationProtocol;

/**
 * @author kyle
 * 
 */
public class DummyConnectionManagerNew extends ConnectionManager {

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
	private DummyCommunicationProtocol communicationProtocol;

	Log logger = LogFactory.getLog(DummyConnectionManagerNew.class);

	public DummyConnectionManagerNew(ReaderInfo readerInfo) {
		super(readerInfo);
		this.readerInfo = readerInfo;
		this.communicationProtocol = new DummyCommunicationProtocol(
				(DummyReaderInfo) readerInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#connect(org.rifidi.edge.core.communication.Connection)
	 */
	@Override
	public void connect(Connection connection) throws RifidiConnectionException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#createCommunication()
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#disconnect(org.rifidi.edge.core.communication.Connection)
	 */
	@Override
	public void disconnect(Connection connection) {
		try {
			connection.sendMessage(new String("quit\n"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			socket.close();
		} catch (IOException e) {
			logger.debug("Socket already closed");
		}

	}

	@Override
	public CommunicationProtocol getCommunicationProtocol() {
		return this.communicationProtocol;
	}

	@Override
	public int getMaxNumConnectionsAttemps() {
		if (this.readerInfo.getMaxNumConnectionsAttemps() > 1) {
			return readerInfo.getMaxNumConnectionsAttemps();
		} else {
			return 1;
		}
	}

	@Override
	public long getReconnectionIntervall() {
		if (this.readerInfo.getReconnectionIntervall() > 0) {
			return readerInfo.getReconnectionIntervall();
		} else {
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#startKeepAlive(org.rifidi.edge.core.communication.Connection)
	 */
	@Override
	public void startKeepAlive(Connection connection) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#stopKeepAlive(org.rifidi.edge.core.communication.Connection)
	 */
	@Override
	public void stopKeepAlive(Connection connection) {
		// TODO Auto-generated method stub

	}

}
