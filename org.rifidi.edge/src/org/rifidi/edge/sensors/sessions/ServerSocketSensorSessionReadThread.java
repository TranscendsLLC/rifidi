/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
/**
 * 
 */
package org.rifidi.edge.sensors.sessions;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Override the read thread to ensure that the socket is closed when the thread
 * is finished executing
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ServerSocketSensorSessionReadThread extends ReadThread {

	/** The socket that the read thread uses */
	private Socket socket;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(ServerSocketSensorSessionReadThread.class);

	/***
	 * Constructor
	 * 
	 * @param clientSocket
	 *            The socket to read from
	 * @param messageParserFactory
	 *            The factory that produces new messageParsingstrategies
	 * @param messageProcessorFactory
	 *            the factory that produces new MessageProcessingStrategies
	 * @throws IOException
	 */
	public ServerSocketSensorSessionReadThread(Socket clientSocket,
			MessageParsingStrategyFactory messageParserFactory,
			MessageProcessingStrategyFactory messageProcessorFactory)
			throws IOException {
		super(clientSocket.getInputStream(), messageParserFactory,
				messageProcessorFactory);
		this.socket = clientSocket;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.base.threads.AbstractReadThread#run()
	 */
	@Override
	public void run() {
		// process messages while this thread has not been interrupted
		try {
			super.run();
		} finally {
			try {
				socket.close();
				logger
						.info("Socket: "
								+ socket.getRemoteSocketAddress()
								+ " closed. Exiting AbstractServerSocketSensorSessionReadThread");
			} catch (IOException e) {
				// ignore
			}
		}
	}

}
