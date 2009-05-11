/*
 *  AlienGetCommandObject.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.alien.commandobject;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readers.ByteMessage;
import org.rifidi.edge.readerplugin.alien.Alien9800ReaderSession;

/**
 * A CommandObject that will get a value from the AlienReader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
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
	 * @param readerSession
	 *            The reader session to execute the command on
	 */
	public AlienGetCommandObject(String command) {
		super(command);
	}

	/**
	 * Constructor
	 * 
	 * @param command
	 *            The command to execute
	 * @param readerSession
	 *            The reader session to execute the command on
	 */
	public AlienGetCommandObject(String command, Alien9800ReaderSession session) {
		super(command);
		super.setSession(session);
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.readerplugin.alien.commandobject.AlienCommandObject#execute()
	 */
	@Override
	public String execute() throws IOException, AlienException {
		String message = Alien9800ReaderSession.PROMPT_SUPPRESS + "get "
				+ command + Alien9800ReaderSession.NEWLINE;

		readerSession.sendMessage(new ByteMessage(message.getBytes()));

		ByteMessage incomingMessage = readerSession.receiveMessage();

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
