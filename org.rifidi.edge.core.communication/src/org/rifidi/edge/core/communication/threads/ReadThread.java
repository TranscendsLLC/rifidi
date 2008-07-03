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

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.thread.AbstractThread;
import org.rifidi.edge.core.communication.protocol.Protocol;
import org.rifidi.edge.core.exception.readerConnection.RifidiInvalidMessageFormat;

/**
 * @author Jerry Maine - jerry@pramari.com
 * 
 */
public class ReadThread extends AbstractThread {
	private static final Log logger = LogFactory.getLog(ReadThread.class);

	private Protocol protocol;
	private LinkedBlockingQueue<Object> readQueue;

	private InputStream inputStream;

	/**
	 * Instantiates a asynchronous read thread.
	 * 
	 * @param threadName
	 *            The Name of the thread.
	 * @param protocol
	 *            The plugin provided protocol used to transform the byte stream
	 *            into objects.
	 * @param readQueue
	 *            The blocking queue used to push the objects created from the
	 *            protocol to the adapter.
	 * @param inputStream
	 *            An input stream from a socket.
	 */
	public ReadThread(String threadName, Protocol protocol,
			LinkedBlockingQueue<Object> readQueue, InputStream inputStream) {
		super(threadName);
		this.protocol = protocol;
		this.readQueue = readQueue;
		this.inputStream = inputStream;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		logger.debug("Starting Read thread");
		try {
			Object message = null;
			int input;
			while ((input = inputStream.read()) != -1 && running) {
				message = protocol.add((byte) input);
				if (message != null) {
					readQueue.add(message);
				}
			}
			// TODO: Think about catching SocketTimeOutException separately.
			/* The exception handler is in the Java standard api
			 * See Thread.setUncaughtExceptionHandler(), 
			 * 	   Thread.getUncaughtExceptionHandler()
			 */
		} catch (IOException e) {
			running = false;
			Thread.currentThread().getUncaughtExceptionHandler()
					.uncaughtException(Thread.currentThread(), e);
		} catch (RifidiInvalidMessageFormat e) {
			running = false;
			Thread.currentThread().getUncaughtExceptionHandler()
					.uncaughtException(Thread.currentThread(), e);
		}
	}


}
