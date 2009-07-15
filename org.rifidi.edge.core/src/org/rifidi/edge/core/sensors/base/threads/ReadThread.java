package org.rifidi.edge.core.sensors.base.threads;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.messages.ByteMessage;

/**
 * Runnable used to read from an inputstream.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class ReadThread implements Runnable {
	/** Logger for this class. */
	private static Log logger = LogFactory.getLog(ReadThread.class);
	/** Reference to the message queue. */
	private Queue<ByteMessage> messageQueue;
	/** The input stream the thread reads from. */
	private InputStream inputStream;
	/** Message Parser used in this thread */
	private MessageParser messageParser;

	/**
	 * Constructor.
	 * 
	 * @param socket
	 * @param messageQueue
	 */
	public ReadThread(MessageParser messageParser, InputStream inputStream,
			Queue<ByteMessage> messageQueue) {
		this.messageQueue = messageQueue;
		this.inputStream = inputStream;
		this.messageParser = messageParser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		logger.info("Starting Read Thread");
		try {
			while (!Thread.interrupted()) {
				int input = inputStream.read();
				if (input == -1) {
					break;
				}
				byte[] message = messageParser.isMessage((byte) input);
				if (message != null) {
					logger.debug("Got message: " + message);
					ByteMessage mes = new ByteMessage(message);
					messageQueue.add(mes);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
		logger.info("Exiting read thread");
	}
}
