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
