/**
 * 
 */
package org.rifidi.edge.core.sensors.base;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.base.threads.MessageParsingStrategyFactory;
import org.rifidi.edge.core.sensors.base.threads.MessageProcessingStrategyFactory;
import org.rifidi.edge.core.sensors.base.threads.ReadThread;

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
	 * @see org.rifidi.edge.core.sensors.base.threads.AbstractReadThread#run()
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
