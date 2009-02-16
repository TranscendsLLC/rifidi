/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.communication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.readerplugin.alien.Alien9800Reader;

/**
 * Runnable used to read from an inputstream.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ReadThread implements Runnable {
	/** Logger for this class. */
	private static Log logger = LogFactory.getLog(ReadThread.class);
	/** Reference to the message queue. */
	private Queue<Object> messageQueue;
	/** The input stream the thread reads from. */
	private InputStream inputStream;
	/** Buffer holding the current message. */
	private StringBuilder buf;

	/**
	 * @param socket
	 * @param messageQueue
	 */
	public ReadThread(InputStream inputStream, Queue<Object> messageQueue) {
		this.messageQueue = messageQueue;
		this.inputStream = inputStream;
		buf = new StringBuilder();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		logger.debug("Starting Read Thread");
		try {
			while (!Thread.interrupted()) {
				int input = inputStream.read();
				if (input == -1) {
					break;
				}
				String message = byteToMessage((byte) input);
				if (message != null) {
					logger.debug("Got message: " + message);
					messageQueue.add(message);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	/**
	 * Attach the given byte to the message.
	 * 
	 * @param b
	 * @return the message or null if the message is not yet complete
	 */
	public String byteToMessage(byte b) {
		buf.append((char) b);
		if ((char) b == Alien9800Reader.TERMINATION_CHAR) {
			String temp = buf.toString();
			buf.delete(0, buf.length());
			return temp;
		}
		return null;
	}
}
