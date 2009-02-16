/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.communication;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Runnable for writing messages to an outputstream.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class WriteThread implements Runnable {
	/** Logger for this class. */
	private static Log logger = LogFactory.getLog(WriteThread.class);
	/** Reference to the message queue. */
	private BlockingQueue<Object> messageQueue;
	/** The input stream the thread reads from. */
	private OutputStream outputStream;
	/** Buffer holding the current message. */
	private StringBuilder buf;

	/**
	 * @param socket
	 * @param messageQueue
	 */
	public WriteThread(OutputStream outputStream,
			BlockingQueue<Object> messageQueue) {
		this.messageQueue = messageQueue;
		this.outputStream = outputStream;
		buf = new StringBuilder();
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
				String message = (String)messageQueue.take();
				outputStream.write(message.getBytes());
			}
		} catch (IOException e) {
			logger.error(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
