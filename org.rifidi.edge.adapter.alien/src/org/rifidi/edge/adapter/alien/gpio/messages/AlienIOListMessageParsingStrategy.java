/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.alien.gpio.messages;

import org.rifidi.edge.adapter.alien.commandobject.AlienException;

/**
 * The alien reader can send out IO events in various formats (Text, XML, Terse,
 * or Custom). We can use the strategy pattern to provide an appropriate method
 * for parsing the message.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AlienIOListMessageParsingStrategy {

	/**
	 * Turn a an unparsed string from the Alien Reader into a parsed
	 * AlienGPIOMessage
	 * 
	 * @param rawMessage
	 *            the unparsed IO message string.
	 * @return A parsed AliengGPIOMessage
	 * @throws AlienException
	 *             If there was a problem when parsing the message
	 */
	public abstract AlienGPIOMessage parseMessage(String rawMessage)
			throws AlienException;

}
