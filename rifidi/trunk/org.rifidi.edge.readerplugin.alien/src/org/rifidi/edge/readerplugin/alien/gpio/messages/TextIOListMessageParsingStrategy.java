/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.gpio.messages;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.readerplugin.alien.messages.AlienMessage;

/**
 * 
 * Parses messages with the following format:
 * 
 * IO:DI, Time:2010/03/08 19:07:38.752, Data:0
 * 
 * @author kyle
 * 
 */
public class TextIOListMessageParsingStrategy extends
		AlienIOListMessageParsingStrategy {

	private static final Log logger = LogFactory
			.getLog(TextIOListMessageParsingStrategy.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.readerplugin.alien.gpio.messages.
	 * AlienIOListMessageParsingStrategy#parseMessage(java.lang.String)
	 */
	@Override
	public AlienGPIOMessage parseMessage(String rawMessage) {
		AlienGPIOMessage message = null;
		try {
			if (rawMessage.startsWith("#")) {
				return new AlienGPIOHeaderMessage();
			}
			String[] fields = rawMessage.split(",");
			String ioEventTypeField = fields[0];
			String timeField = fields[1];
			String dataField = fields[2];

			if (ioEventTypeField.split(":")[1].equals("DI")) {
				message = new AlienGPIMessage();
			} else {
				message = new AlienGPOMessage();
			}

			String timeString = timeField.split(":", 2)[1];
			Date date = AlienMessage.parseAlienDate(timeString);
			if (date != null)
				message.setTimestamp(date.getTime());
			else {
				logger.error("Error with timestring: " + timeString);
			}

			String dataString = dataField.split(":")[1];
			message.setData(Integer.parseInt(dataString.trim()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return message;
	}

}
