/**
 * 
 */
package org.rifidi.edge.core.readers.impl.threads;
//TODO: Comments
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readers.ByteMessage;

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
				outputStream
						.write(((ByteMessage) messageQueue.take()).message);
			}
		} catch (IOException e) {
			logger.error(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
