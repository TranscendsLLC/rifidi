package org.rifidi.edge.readerplugin.thingmagic.plugin;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionStreams;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;
import org.rifidi.edge.readerplugin.thingmagic.protocol.ThingMagicCommunicationProtocol;

/**
 * @author Jerry Maine - jerry@pramari.com
 * 
 */

public class ThingMagicManager extends ConnectionManager {
	private static final Log logger = LogFactory
			.getLog(ThingMagicManager.class);

	public ThingMagicManager(ReaderInfo readerInfo) {
		super(readerInfo);
		info = (ThingMagicReaderInfo) readerInfo;
	}

	private Socket socket;
	private ThingMagicReaderInfo info;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#getCommunicationProtocol()
	 */
	@Override
	public CommunicationProtocol getCommunicationProtocol() {
		return new ThingMagicCommunicationProtocol();
	}

	@Override
	public int getMaxNumConnectionsAttemps() {
		return (info.getMaxNumConnectionsAttemps() < 0) ? info
				.getMaxNumConnectionsAttemps() : 3;
	}

	@Override
	public long getReconnectionIntervall() {
		return (info.getReconnectionIntervall() < 0) ? info
				.getReconnectionIntervall() : 1000;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#startKeepAlive()
	 */
	@Override
	public void startKeepAlive(Connection connectoin) {
		// ignore this.

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#stopKeepAlive()
	 */
	@Override
	public void stopKeepAlive(Connection connection) {
		// ignore this.

	}

	@Override
	public ConnectionStreams createCommunication()
			throws RifidiConnectionException {
		if ( ( (info.getPassword() == null) || info.getPassword().equals("")  ) &&
			 ( (info.getUser() == null)     || info.getUser().equals("")      )     ) {
			//logger.debug("Trying to connect to: " + info.getIpAddress() + ":"
			//		+ info.getPort() + ".");
			try {
				socket = new Socket(info.getIpAddress(), info.getPort());
			} catch (UnknownHostException e) {
				// logger.debug("Error: ", e);
				throw new RifidiConnectionException(e);
			} catch (IOException e) {
				// logger.debug("Error: ", e);
				throw new RifidiConnectionException(e);
			}
		} else if (( (info.getPassword() != null) && !info.getPassword().equals("")  ) &&
				   ( (info.getUser() != null)     && !info.getUser().equals("")      )     ){
			// TODO implement connection to reader by ssh
			throw new UnsupportedOperationException(
					"Connections to Merucry 4 or 5, ThingMagic readers by ssh not impemented.");
		} else {
			throw new IllegalStateException("Password and username in invalid state.");
		}

		try {
			return new ConnectionStreams(socket.getInputStream(), socket
					.getOutputStream());
		} catch (IOException e) {
			// logger.debug("Error: ", e);
			throw new RifidiConnectionException(e);
		}
	}

	@Override
	public void connect(Connection connection) throws RifidiConnectionException {
		// Do nothing

	}

	@Override
	public void disconnect(Connection connection) {

		try {
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
