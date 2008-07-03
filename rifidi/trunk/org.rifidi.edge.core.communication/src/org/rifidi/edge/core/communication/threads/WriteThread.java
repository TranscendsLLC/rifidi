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

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.protocol.Protocol;
import org.rifidi.edge.core.exception.readerConnection.RifidiInvalidMessageFormat;
import org.rifidi.edge.common.utilities.thread.AbstractThread;

public class WriteThread extends AbstractThread {
	private static final Log logger = LogFactory.getLog(WriteThread.class);

	private Protocol protocol;
	private LinkedBlockingQueue<Object> writeQueue;
	private OutputStream outputStream;

	/**
	 * Instantiates a asynchronous read thread.
	 * @param threadName The Name of the thread.
	 * @param protocol The plugin provided protocol used to transform objects into a byte stream.
	 * @param writeQueue The blocking queue used to pull the objects created from adapter through the protocol to the socket.
	 * @param outputStream The output stream of the socket.
	 */
	public WriteThread(String threadName, Protocol protocol,
			LinkedBlockingQueue<Object> writeQueue, OutputStream outputStream) {
		super(threadName);

		this.protocol = protocol;
		this.writeQueue = writeQueue;
		this.outputStream = outputStream;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		logger.debug("Starting Write thread");
		try {
			while (running) {

				Object object = writeQueue.take();
				logger.debug(object);
				byte[] bytes = protocol.toByteArray(object);

				outputStream.write(bytes);
				outputStream.flush();
			}
		} catch (InterruptedException e) {
			running = false;
			// e.printStackTrace();
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
