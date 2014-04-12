/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
/**
 * 
 */
package org.rifidi.edge.sensors.sessions;

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
