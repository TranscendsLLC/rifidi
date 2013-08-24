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
package org.rifidi.edge.adapter.alien;

import java.util.LinkedList;
import java.util.List;

import org.rifidi.edge.sensors.sessions.MessageParsingStrategy;

/**
 * This object parses alien messages that come in byte by byte, for example from
 * a socket. When an entire message has been received, it returns the message.
 * Note that it keeps an internal state, so there is a one to one correspondence
 * between this message parser and an input source (such as a socket). In other
 * words, create a new instance of this object for each socket that is created
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienMessageParsingStrategy implements MessageParsingStrategy {

	
	/** Character that terminates a message from alien. */
	public static final char TERMINATION_CHAR = '\0';
	/** The message currently being processed. */
	private List<Byte> builder = new LinkedList<Byte>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.threads.MessageParser#isMessage(byte)
	 */
	@Override
	public byte[] isMessage(byte message) {
		if (TERMINATION_CHAR == message) {
			byte[] ret = new byte[builder.size()];
			int index=0;
			for(Byte b:builder) {
				ret[index]=b;
				index++;
			}
			builder.clear();
			return ret;
		}
		builder.add(message);
		return null;
	}

}
