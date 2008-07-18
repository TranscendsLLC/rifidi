/*
 *  WriteThread.java
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

import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionExceptionListener;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

/**
 * Write Thread listens on the queue and writes message to the OutputStream. It
 * uses the Protocol provided by a ReaderPlugin to transform the messages
 * received from the queue into byte arrays and transmit them. It notifies a
 * Listener if there was a Exception on the underlying communication.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class WriteThread extends AbstractThread {
	private static final Log logger = LogFactory.getLog(WriteThread.class);

	private CommunicationProtocol protocol;
	private LinkedBlockingQueue<Object> writeQueue;
	private OutputStream outputStream;
	private boolean ignoreExceptions = false;
	private ConnectionExceptionListener connectionExceptionListener;

	/**
	 * Create a new WriteThread
	 * 
	 * @param threadName
	 *            the name of the thread
	 * @param connectionExceptionListener
	 *            listener of exceptions on the underlying communication
	 * @param protocol
	 *            the protocol provided by
	 * @param writeQueue
	 * @param outputStream
	 */
	public WriteThread(String threadName,
			ConnectionExceptionListener connectionExceptionListener,
			CommunicationProtocol protocol, Queue<Object> writeQueue,
			OutputStream outputStream) {
		super(threadName);

		this.protocol = protocol;
		// TODO make this more safe
		this.writeQueue = (LinkedBlockingQueue<Object>) writeQueue;
		this.outputStream = outputStream;
		this.connectionExceptionListener = connectionExceptionListener;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		logger.debug("Starting: " + Thread.currentThread().getName());
		try {
			while (running) {
				Object object = writeQueue.take();
				logger.debug(object);
				byte[] bytes = protocol.messageToByte(object);

				outputStream.write(bytes);
				outputStream.flush();
			}
		} catch (InterruptedException e) {
			running = false;
		} catch (Exception e) {
			running = false;
			logger.debug(e.getMessage());
			if (!ignoreExceptions) {
				connectionExceptionListener.connectionExceptionEvent(e);
			}
		}
		logger.debug("Stopping: " + Thread.currentThread().getName());
	}

}
