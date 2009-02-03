/*
 *  ReadThread.java
 *
 *  Created:	Jun 27, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.communication.threads;

import java.io.InputStream;
import java.util.Queue;

import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.api.readerplugin.communication.CommunicationProtocol;
import org.rifidi.edge.core.communication.service.ConnectionExceptionListener;


/**
 * ReadThread constantly reading from the given InputStream parsing the read
 * bytes with help form the CommunicationProtocol of a ReaderPlugin and storing
 * the full messages on a queue.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class ReadThread extends AbstractThread {
	private static final org.apache.commons.logging.Log logger = LogFactory
			.getLog(ReadThread.class);

	private CommunicationProtocol protocol;
	private Queue<Object> readQueue;

	private InputStream inputStream;

	private ConnectionExceptionListener connectionExceptionListener;

	/**
	 * Create a new ReadThread
	 * 
	 * @param threadName
	 *            the name of the Thread
	 * @param connectionExceptionListener
	 *            listener for communication exceptions
	 * @param protocol
	 *            protocol to transform bytes read from the InputStream into messages
	 * @param readQueue
	 *            the queue to store the read messages on
	 * @param inputStream
	 *            stream to read from
	 */
	public ReadThread(String threadName,
			ConnectionExceptionListener connectionExceptionListener,
			CommunicationProtocol protocol, Queue<Object> readQueue,
			InputStream inputStream) {
		super(threadName);
		this.protocol = protocol;
		this.readQueue = readQueue;
		this.inputStream = inputStream;
		this.connectionExceptionListener = connectionExceptionListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		logger.debug("Starting: " + Thread.currentThread().getName());
		try {
			Object message = null;
			int input;
			while ((input = inputStream.read()) != -1 && running) {
				message = protocol.byteToMessage((byte) input);
				if (message != null) {
					//logger.debug(message);
					readQueue.add(message);
				}
			}
			if (!ignoreExceptions) {
				connectionExceptionListener
						.connectionExceptionEvent(new RifidiConnectionException(
								"Socket was closed by client"));
			}
		} catch (Exception e) {
			running = false;
			logger.debug("Error when reading");
			if (!ignoreExceptions) {
				connectionExceptionListener.connectionExceptionEvent(e);
			}
		}
		logger.debug("Stopping: " + Thread.currentThread().getName());
	}

}
