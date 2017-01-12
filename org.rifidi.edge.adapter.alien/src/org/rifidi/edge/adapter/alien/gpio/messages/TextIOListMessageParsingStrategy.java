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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.adapter.alien.commandobject.AlienException;
import org.rifidi.edge.adapter.alien.messages.AlienMessage;

/**
 * A strategy for parsing alien IO messages in the text format.
 * 
 * Parses messages with the following format:
 * 
 * IO:DI, Time:2010/03/08 19:07:38.752, Data:0
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TextIOListMessageParsingStrategy extends
		AlienIOListMessageParsingStrategy {

	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(TextIOListMessageParsingStrategy.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.readerplugin.alien.gpio.messages.
	 * AlienIOListMessageParsingStrategy#parseMessage(java.lang.String)
	 */
	@Override
	public AlienGPIOMessage parseMessage(String rawMessage)
			throws AlienException {
		AlienGPIOMessage message = null;
		try {

			// if the message starts with #, it's a header method. We ignore
			// these for now.
			if (rawMessage.startsWith("#")) {
				return new AlienGPIOHeaderMessage();
			}

			// spit the string at the ',' charater
			String[] fields = rawMessage.split(",");

			// [IO:<EventType>]
			String ioEventTypeField = fields[0];
			// [ Time:<Timestamp>]
			String timeField = fields[1];
			// [ Data:<Integer>]
			String dataField = fields[2];

			// split the eventtypefield at the ':' so we have [DI][<EventType>]
			if (ioEventTypeField.split(":")[1].equals("DI")) {
				message = new AlienGPIMessage();
			} else {
				message = new AlienGPOMessage();
			}

			// split the timefield at the first ':' so we have [Time][<Time>]
			String timeString = timeField.split(":", 2)[1];
			Date date = AlienMessage.parseAlienDate(timeString);
			if (date != null)
				message.setTimestamp(date.getTime());
			else {
				logger.error("Error with timestring: " + timeString);
			}

			// split the datafield at the ':' so we have [Data][<Integer>]
			String dataString = dataField.split(":")[1];
			message.setData(Integer.parseInt(dataString.trim()));
		} catch (Exception e) {
			throw new AlienException("Cannot parse IO message: " + message);
		}

		return message;
	}

}
