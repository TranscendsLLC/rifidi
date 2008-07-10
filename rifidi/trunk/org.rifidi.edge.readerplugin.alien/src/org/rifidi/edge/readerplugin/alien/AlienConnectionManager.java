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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class AlienConnectionManager extends ConnectionManager {

	private static final Log logger = LogFactory
			.getLog(AlienConnectionManager.class);
	private AlienReaderInfo info;

	private Socket alienSock;

	private BufferedReader reader;

	private PrintWriter writer;

	private AlienCommunicationProtocol protocol;

	public static String PROMPT_SUPPRESS = "\1";

	/**
	 * 
	 * @param readerInfo
	 */
	public AlienConnectionManager(ReaderInfo readerInfo) {
		super(readerInfo);
		this.info = (AlienReaderInfo) readerInfo;
		this.alienSock = null;
		this.reader = null;
		this.writer = null;
		this.protocol = (AlienCommunicationProtocol) this
				.getCommunicationProtocol();
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
				this.readFromReader(reader);
				this.writeToReader(PROMPT_SUPPRESS + this.info.getUsername());
				this.readFromReader(reader);
				this.writeToReader(PROMPT_SUPPRESS + this.info.getPassword());
				this.readFromReader(reader);
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
			alienSock = new Socket(this.info.getIpAddress(), this.info
					.getPort());
			reader = new BufferedReader(new InputStreamReader(alienSock
					.getInputStream()));
			writer = new PrintWriter(alienSock.getOutputStream());
			cs = new ConnectionStreams(alienSock.getInputStream(), alienSock
					.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
			this.writeToReader("q");
			this.readFromReader(reader);
			this.writer.close();
			this.reader.close();
			this.alienSock.close();
		} catch (IOException e) {
			e.printStackTrace();
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
	public void startKeepAlive() {
		// TODO Nothing here yet, more later

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#stopKeepAlive()
	 */
	@Override
	public void stopKeepAlive() {
		// TODO Nothing here yet, more later

	}

	/**
	 * Writes the message to the reader.
	 * 
	 * @param message
	 */
	private void writeToReader(String message) {
		byte[] bytes = this.protocol.messageToByte(message);
		this.writer.print(bytes);
	}

	/**
	 * Read responses from the socket
	 * 
	 * @param inBuf
	 * @return
	 * @throws IOException
	 */
	public String readFromReader(BufferedReader inBuf) throws IOException {
		StringBuffer buf = new StringBuffer();
		logger.debug("Reading...");
		int ch = inBuf.read();
		String message = (String) this.protocol.byteToMessage((byte) ch);
		while (message != null) {
			ch = inBuf.read();
			message = (String) this.protocol.byteToMessage((byte) ch);
		}
		logger.debug("Done reading!");
		logger.debug("Reading in: " + buf.toString());
		return buf.toString();
	}
}
