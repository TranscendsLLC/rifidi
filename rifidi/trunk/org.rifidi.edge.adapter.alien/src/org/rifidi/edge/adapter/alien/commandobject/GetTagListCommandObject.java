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
package org.rifidi.edge.adapter.alien.commandobject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.rifidi.edge.adapter.alien.Alien9800ReaderSession;
import org.rifidi.edge.adapter.alien.messages.AlienTag;
import org.rifidi.edge.sensors.ByteMessage;

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
