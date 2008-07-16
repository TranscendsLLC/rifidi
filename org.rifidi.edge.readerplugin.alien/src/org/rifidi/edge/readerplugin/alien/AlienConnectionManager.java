/* 
 * AlienConnectionManager.java
 *  Created:	Jul 9, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.alien;

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
import org.rifidi.edge.readerplugin.alien.protocol.AlienCommunicationProtocol;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AlienConnectionManager extends ConnectionManager {

	private static final Log logger = LogFactory
			.getLog(AlienConnectionManager.class);
	private AlienReaderInfo info;

	private Socket alienSock;

	public static String PROMPT_SUPPRESS = "\1";

	public static String NEWLINE = "\n";

	/**
	 * 
	 * @param readerInfo
	 */
	public AlienConnectionManager(ReaderInfo readerInfo) {
		super(readerInfo);
		this.info = (AlienReaderInfo) readerInfo;
		this.alienSock = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#connect()
	 */
	@Override
	public void connect(Connection connection) throws RifidiConnectionException {
		logger.debug("Alien is connecting");
		if (alienSock != null) {
			try {
				logger.debug("getting the welcome response");
				connection.receiveMessage();
				logger.debug("sending username");
				connection.sendMessage(PROMPT_SUPPRESS
						+ this.info.getUsername() + NEWLINE);
				logger.debug("getting the username response");
				connection.receiveMessage();
				logger
						.debug("sending the password: "
								+ this.info.getPassword());
				connection.sendMessage(PROMPT_SUPPRESS
						+ this.info.getPassword() + NEWLINE);
				logger.debug("recieving the password response");
				connection.receiveMessage();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new RifidiConnectionException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#createCommunication()
	 */
	@Override
	public ConnectionStreams createCommunication()
			throws RifidiConnectionException {
		// TODO: Check if the IP and port are valid here
		ConnectionStreams cs = null;
		try {
			logger.debug("Creating the communication, IP is: "
					+ this.info.getIpAddress() + ", and port is: "
					+ this.info.getPort());
			alienSock = new Socket(this.info.getIpAddress(), this.info
					.getPort());
			logger.debug("Done creating the socket");
			cs = new ConnectionStreams(alienSock.getInputStream(), alienSock
					.getOutputStream());
		} catch (UnknownHostException e) {
			throw new RifidiConnectionException(e.getMessage());
		} catch (IOException e) {
			throw new RifidiConnectionException(e.getMessage());
		}
		return cs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#disconnect()
	 */
	@Override
	public void disconnect(Connection connection) {
		try {
			connection.sendMessage("q" + NEWLINE);
			connection.receiveMessage(500);
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally
		{
			if (this.alienSock != null) {
				try {
					this.alienSock.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#getCommunicationProtocol()
	 */
	@Override
	public CommunicationProtocol getCommunicationProtocol() {
		return new AlienCommunicationProtocol();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#getMaxNumConnectionsAttemps()
	 */
	@Override
	public int getMaxNumConnectionsAttemps() {
		if (info.getMaxNumConnectionsAttemps() <= 0) {
			return 1000;
		}
		return info.getMaxNumConnectionsAttemps();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#getReconnectionIntervall()
	 */
	@Override
	public long getReconnectionIntervall() {
		if (info.getReconnectionIntervall() <= 0) {
			return 1000;
		}
		return info.getMaxNumConnectionsAttemps();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#startKeepAlive()
	 */
	@Override
	public void startKeepAlive(Connection connection) {
		// TODO Nothing here yet, more later

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#stopKeepAlive()
	 */
	@Override
	public void stopKeepAlive(Connection connection) {
		// TODO Nothing here yet, more later

	}
}
