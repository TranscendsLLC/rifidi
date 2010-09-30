/*
 * 
 * ReadThread.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.core.sensors.sessions;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class of a runnable used to read from an inputstream. It provides two
 * important hooks: a MessageParsingStrategy and a MessageProcessingStrategy.
 * The MessageParsingStrategy decides when the bytes that have been read from
 * the inputStream form an implementation-specific message. The
 * MessageProcessingStrategy class allows implementations to provide an
 * implementation-specific action that should happen when the message has been
 * completely received
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReadThread implements Runnable {

	private static final Log logger = LogFactory.getLog(ReadThread.class);
	/** The input stream the thread reads from. */
	private InputStream inputStream;
	/** Message Parser used in this thread */
	private MessageParsingStrategyFactory messageParserFactory;
	/** Message Processor to be used in this thread */
	private MessageProcessingStrategy messageProcessor;

	/**
	 * Constructor
	 * 
	 * @param inputStream
	 *            The input stream from which to read
	 * @param messageParserFactory
	 *            a factory to produce a new message parsing strategy
	 * @param messageProcessorFactory
	 *            a factory to produce new message processing strategy
	 */
	public ReadThread(InputStream inputStream,
			MessageParsingStrategyFactory messageParserFactory,
			MessageProcessingStrategyFactory messageProcessorFactory) {
		this.inputStream = inputStream;
		this.messageProcessor = messageProcessorFactory
				.createMessageProcessor();
		this.messageParserFactory = messageParserFactory;
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
			MessageParsingStrategy messageParser = this.messageParserFactory
					.createMessageParser();
			while (!Thread.interrupted()) {

				int input = inputStream.read();
				if (input == -1) {
					break;
				}
				byte[] message = messageParser.isMessage((byte) input);
				if (message != null) {
					messageProcessor.processMessage(message);
					messageParser = messageParserFactory.createMessageParser();
				}
			}
		} catch (IOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			logger.debug("Exiting read thread");
		}
	}

}
