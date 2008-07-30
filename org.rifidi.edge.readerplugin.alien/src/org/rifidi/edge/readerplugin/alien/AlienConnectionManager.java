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
 * This class handles all of the connecting/disconnecting with the Alien reader.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AlienConnectionManager extends ConnectionManager {

	public static final String WELCOME = "Alien";
	
	/**
	 * Logger.
	 */
	private static final Log logger = LogFactory
			.getLog(AlienConnectionManager.class);

	/**
	 * The ReaderInfo that we will use to get the username and password.
	 */
	private AlienReaderInfo info;

	/**
	 * The socket for the Alien reader.
	 */
	private Socket alienSock;

	/**
	 * You can put this in front of a Alien command for terse output to come
	 * back to you, making things faster and easier to parse.
	 */
	public static String PROMPT_SUPPRESS = "\1";

	/**
	 * Attach this to anything you send to the Alien.
	 */
	public static String NEWLINE = "\n";

	private KeepAliveThread aka = null;

	/**
	 * Constructor for the AlienConnectionManager.
	 * 
	 * @param readerInfo
	 */
	public AlienConnectionManager(ReaderInfo readerInfo) {
		super(readerInfo);
		this.info = (AlienReaderInfo) readerInfo;
		this.alienSock = null;
		this.aka = new KeepAliveThread();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#connect()
	 */
	@Override
	public void connect(Connection connection) throws RifidiConnectionException {
		//TODO: Check if this is really an Alien reader
		logger.debug("Alien is connecting");
		if (alienSock != null) {
			try {
				logger.debug("getting the welcome response");
				String welcome = (String) connection.receiveMessage();
				if (welcome == null || !welcome.contains(WELCOME)) {
					logger.debug("RifidiConnectionException was thrown,"
							+ " reader is not an alien reader: " + welcome);
					throw new RifidiConnectionException(
							"Reader is not an alien reader");
				} else if (welcome.toLowerCase().contains("busy")){
					throw new RifidiConnectionException(welcome);
				} else {
					logger.debug("Reader is an alien.  Hoo-ray!");
				}
				
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
		} finally {
			if (this.alienSock != null) {
				try {
					this.alienSock.close();
				} catch (IOException e) {
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
		 aka.connection = connection;
		 aka.running = true;
		 aka.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#stopKeepAlive()
	 */
	@Override
	public void stopKeepAlive(Connection connection) {
		 aka.running = false;
	}

	/**
	 * This thread constantly polls an Alien reader with a version command,
	 * keeping it from timing out.
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private class KeepAliveThread extends Thread {

		public static final String SET_KEEP_ALIVE = "set NotifyKeepAliveTime=30000";

		/**
		 * Tells whether the thread should keep running or not.
		 */
		public boolean running = false;

		/**
		 * The connection that we will send our keep alive pings through.
		 */
		public Connection connection = null;

		/**
		 * Constructor for the keep alive thread.
		 * 
		 * @param connection
		 */
		public KeepAliveThread() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			while (running) {
				try {
					logger.debug("sending keep alive");
					connection.sendMessage(PROMPT_SUPPRESS + SET_KEEP_ALIVE
							+ NEWLINE);
					connection.receiveMessage();
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {/* Do nothing */
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
