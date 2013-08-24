/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
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
