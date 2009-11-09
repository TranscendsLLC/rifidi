/*
 * IncomingAwidMessageFactory.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.readerplugin.awid.awid2010.communication.messages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is a factory that parses a raw awid byte message into a message object
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class IncomingAwidMessageFactory {

	/** The readerID this factory is associated with */
	private final String readerID;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(IncomingAwidMessageFactory.class);

	/**
	 * @param readerID
	 */
	public IncomingAwidMessageFactory(String readerID) {
		super();
		this.readerID = readerID;
	}

	/**
	 * This method turns an awid message from a byte[] to an object
	 * 
	 * @param message
	 *            A byte[] that is an awid message
	 * @return An object that subclasses AbstractAwidMessage
	 * @throws InvalidAwidMessageException
	 *             If the byte[] is invalid
	 */
	public AbstractAwidMessage getMessage(byte[] message)
			throws InvalidAwidMessageException {
		if (message == null || message.length == 0) {
			logger.warn("Throwing an InvalidAwidMessageException due to null message or 0 length message");
			throw new InvalidAwidMessageException();
		}

		if (message.length == 1) {
			if (message[0] == (byte) 0x00 || message[0] == (byte) 0xFF) {
				return new AckMessage(message);
			} else {
				logger.warn("Throwing an InvalidAwidMessageException due to malforned acknowledgement");
				throw new InvalidAwidMessageException();
			}
		} else if (message[0] == (byte) 'i' && message[1] == (byte) 'i') {
			return new WelcomeMessage(message);
		} else if (message[1] == 0x20 && message[2] == 0x1E) {
			return new Gen2PortalIDResponse(message, readerID);
		} else if (message[1] == (byte) 0xFF && message[2] == 0x1E) {
			return new PortalIDError(message);
		} else if (message.length > 3) {
			String byte0 = Integer.toHexString(message[0]);
			String byte1 = Integer.toHexString(message[1]);
			String byte2 = Integer.toHexString(message[2]);
			logger.debug("Unknown message header: " + byte0 + " " + byte1 + " "
					+ byte2);
		}
		throw new InvalidAwidMessageException();
	}
}
