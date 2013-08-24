/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
/**
 * 
 */
package org.rifidi.edge.adapter.alien.commandobject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.adapter.alien.Alien9800ReaderSession;
import org.rifidi.edge.sensors.ByteMessage;

/**
 * A CommandObject that will get a value from the AlienReader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienGetCommandObject extends AlienCommandObject {

	/** A logger for this class */
	private static final Log logger = LogFactory
			.getLog(AlienGetCommandObject.class);

	/***
	 * Constructor
	 * 
	 * @param command
	 *            The command to execute
	 */
	public AlienGetCommandObject(String command) {
		super(command);
	}

	@Override
	public String execute() throws IOException, AlienException,
			TimeoutException {
		String message = Alien9800ReaderSession.PROMPT_SUPPRESS + "get "
				+ command + Alien9800ReaderSession.NEWLINE;
		readerSession.sendMessage(new ByteMessage(message.getBytes()));

		ByteMessage incomingMessage;
		try {
			incomingMessage = readerSession.receiveMessage();
		} catch (TimeoutException e) {
			logger.warn("Timeout when waiting for the " + command
					+ " command to execute");
			throw e;
		}

		String incoming = new String(incomingMessage.message);
		if (incoming.contains("=")) {
			String[] splitString = incoming.split("=");
			return splitString[1].trim();
		} else if (incoming.contains("Error")) {
			logger.warn("Error while Getting Property: " + incoming.trim());
			throw new AlienException(incoming.trim());
		} else {
			logger.warn("Something is wrong with the command: "
					+ incoming.trim());
			throw new AlienException(incoming.trim());
		}

	}

}
