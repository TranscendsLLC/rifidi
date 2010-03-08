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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.rifidi.edge.core.sensors.messages.ByteMessage;
import org.rifidi.edge.readerplugin.alien.Alien9800ReaderSession;
import org.rifidi.edge.readerplugin.alien.messages.AlienTag;

public class GetTagListCommandObject {
	/** The sensorSession to send the command to */
	private Alien9800ReaderSession readerSession;

	/**
	 * Construct a command object.
	 * 
	 * @param sensorSession
	 *            A live sensorSession to send the command to
	 * @param timeout
	 *            The amount of time to wait on a response
	 */
	public GetTagListCommandObject(Alien9800ReaderSession readerSession) {
		this.readerSession = readerSession;
	}

	/**
	 * Execute A get
	 * 
	 * @return the value of the property on the alien sensorSession
	 * @throws IOException
	 */
	public List<AlienTag> executeGet() throws IOException, AlienException,
			TimeoutException {
		String message = Alien9800ReaderSession.PROMPT_SUPPRESS + "get "
				+ Alien9800ReaderSession.COMMAND_TAG_LIST
				+ Alien9800ReaderSession.NEWLINE;

		readerSession.sendMessage(new ByteMessage(message.getBytes()));

		ByteMessage incomingMessage;

		incomingMessage = readerSession.receiveMessage();

		String incoming = new String(incomingMessage.message).trim();

		ArrayList<AlienTag> tags = new ArrayList<AlienTag>();

		String[] lines = incoming.split("\n");
		if (lines.length == 0) {
			throw new AlienException(
					"Error while parsing return from Get TagList");
		}
		if (incoming.toLowerCase().contains("(no tags)")) {
			return tags;
		} else {
			for (String line : lines) {
				tags.add(new AlienTag(line));
			}
		}
		return tags;
	}

}
