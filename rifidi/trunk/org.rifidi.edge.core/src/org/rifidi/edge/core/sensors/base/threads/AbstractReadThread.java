/**
 * 
 */
package org.rifidi.edge.core.sensors.base.threads;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class of a runnable used to read from an inputstream. It provides two
 * important hooks: a MessageParsingStrategy and an abstract processMessage
 * method. The MessageParsingStrategy decides when the bytes that have been read
 * from the inputStream form an implementation-specific message. The
 * processMessage method allows implementations to provide an
 * implementation-specific action that should happen when the message has been
 * completely received
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractReadThread implements Runnable {

	private static final Log logger = LogFactory
			.getLog(AbstractReadThread.class);
	/** The input stream the thread reads from. */
	private InputStream inputStream;
	/** Message Parser used in this thread */
	private MessageParsingStrategy messageParser;

	public AbstractReadThread(InputStream inputStream,
			MessageParsingStrategy messageParser) {
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
					processMessage(message);
				}
			}
		} catch (IOException e) {
			logger.error(e);
		}
		logger.info("Exiting read thread");
	}

	/**
	 * A hook to allow implementations to do do something with the message when
	 * it has been completely received from the socket
	 * 
	 * @param message
	 *            The complete message
	 */
	protected abstract void processMessage(byte[] message);

}
