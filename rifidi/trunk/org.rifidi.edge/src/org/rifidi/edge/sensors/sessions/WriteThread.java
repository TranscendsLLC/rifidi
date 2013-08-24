/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.sensors.sessions;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketOptions;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.sensors.ByteMessage;

/**
 * Runnable for writing messages to an outputstream.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class WriteThread implements Runnable {
	/** Logger for this class. */
	private static Log logger = LogFactory.getLog(WriteThread.class);
	/** Reference to the message queue. */
	private BlockingQueue<ByteMessage> messageQueue;
	/** The input stream the thread reads from. */
	private OutputStream outputStream;

	/**
	 * @param socket
	 * @param messageQueue
	 */
	public WriteThread(OutputStream outputStream,
			BlockingQueue<ByteMessage> messageQueue) {
		this.messageQueue = messageQueue;
		this.outputStream = outputStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		logger.debug("Starting Write Thread");
		try {
			while (!Thread.interrupted()) {
				ByteMessage m = messageQueue.take();
				//SocketOutputStream sos = (SocketOu)
				outputStream.write(m.message);
				outputStream.flush();
			}
		} catch (IOException e) {
			logger.error(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
