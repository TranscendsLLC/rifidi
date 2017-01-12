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
