/* 
 * AlienTagStreamCommand.java
 *  Created:	Jul 7, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.alien.commands;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.dynamicswtforms.xml.annotaions.Form;
import org.rifidi.dynamicswtforms.xml.annotaions.FormElement;
import org.rifidi.dynamicswtforms.xml.constants.FormElementType;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.api.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.api.readerplugin.Command;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.api.readerplugin.communication.Connection;
import org.rifidi.edge.core.api.readerplugin.messageQueue.MessageQueue;
import org.rifidi.edge.core.api.readerplugin.messages.impl.EnhancedTagMessage;
import org.rifidi.edge.core.api.readerplugin.messages.impl.TagMessage;
import org.rifidi.edge.readerplugin.alien.properties.AlienTagGenerations;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
@Form(name = "AlienTagStreamCommand", formElements = {
		@FormElement(type = FormElementType.CHOICE, elementName = AlienTagStreamCommand.TAG_TYPE, displayName = AlienTagStreamCommand.TAG_TYPE_DISPLAY, defaultValue = "ALL", enumClass = "org.rifidi.edge.readerplugin.alien.properties.AlienTagGenerations"),
		@FormElement(type = FormElementType.STRING, elementName = AlienTagStreamCommand.ANTENNA_SEQUENCE, displayName = AlienTagStreamCommand.ANTENNA_SEQUENCE_DISPLAY, defaultValue = "0"),
		@FormElement(type = FormElementType.INTEGER, elementName = AlienTagStreamCommand.POLL_PERIOD_IN_MILLIS, displayName = AlienTagStreamCommand.POLL_PERIOD_IN_MILLIS_DISPLAY, defaultValue = AlienTagStreamCommand.POLL_PERIOD_DEFAULT_STRING) })
public class AlienTagStreamCommand implements Command {

	/**
	 * 
	 */
	private static final String TAG_TYPE = "TagType";

	/**
	 * 
	 */
	private static final String TAG_TYPE_DISPLAY = "Tag Type";

	/**
	 * 
	 */
	private static final String ANTENNA_SEQUENCE = "AntennaSequence";

	/**
	 * 
	 */
	private static final String ANTENNA_SEQUENCE_DISPLAY = "Antenna Sequence";

	/**
	 * 
	 */
	private static final String POLL_PERIOD_IN_MILLIS = "PollPeriodInMillis";

	/**
	 * 
	 */
	private static final String POLL_PERIOD_IN_MILLIS_DISPLAY = "Poll Period Millis";

	private static final String POLL_PERIOD_DEFAULT_STRING = "1000";

	private static final int TAG_TYPE_ALL = 31;

	private static final int TAG_TYPE_GEN2 = 16;

	private static final int TAG_TYPE_GEN1 = 7;

	/**
	 * 
	 */
	private boolean running = false;

	private TimeZone timeZone;
	private Calendar calendar;

	private static final Log logger = LogFactory
			.getLog(AlienTagStreamCommand.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readerplugin.commands.Command#start(org.rifidi.edge
	 * .core.communication.Connection,
	 * org.rifidi.edge.core.messageQueue.MessageQueue,
	 * org.rifidi.edge.core.messageQueue.MessageQueue,
	 * org.rifidi.edge.core.readerplugin.commands.CommandConfiguration, long)
	 */
	@Override
	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, MessageQueue errorQueue,
			CommandConfiguration configuration, long commandID) {
		logger.debug("Starting the Tag List command for the Alien");
		running = true;

		// Bounds checking:

		int tag_type_int = -1;
		int poll_period_int = -1;

		if (configuration.getArgValue(TAG_TYPE)
				.equals(AlienTagGenerations.GEN1)) {
			tag_type_int = TAG_TYPE_GEN1;
		} else if (configuration.getArgValue(TAG_TYPE).equals(
				AlienTagGenerations.GEN2)) {
			tag_type_int = TAG_TYPE_GEN2;
		} else {
			tag_type_int = TAG_TYPE_ALL;
		}

		try {
			poll_period_int = Integer.parseInt(configuration
					.getArgValue(POLL_PERIOD_IN_MILLIS));
		} catch (NumberFormatException e) {
			poll_period_int = Integer.parseInt(POLL_PERIOD_DEFAULT_STRING);
		}

		try {
			connection.sendMessage(AlienCommandList.GET_TIME_ZONE);
			String temp = ((String) connection.receiveMessage());
			if (temp.contains("=")) {
				String temp1[] = temp.split("=");
				temp = temp1[1];
			}
			String timeZoneString = "GMT" + temp.trim();

			timeZone = TimeZone.getTimeZone(timeZoneString);

			calendar = Calendar.getInstance(timeZone);

			// send Antenna Sequence
			String command = AlienCommandList.ANTENNA_SEQUENCE_COMMAND
					+ configuration
							.getArgValue(AlienTagStreamCommand.ANTENNA_SEQUENCE)
					+ AlienCommandList.ENDING;

			logger.debug("Sending command: " + command);
			connection.sendMessage(command);
			connection.receiveMessage();

			// sending TagType
			command = AlienCommandList.TAG_TYPE_COMMAND + tag_type_int
					+ AlienCommandList.ENDING;
			logger.debug("Sening command: " + command);
			connection.sendMessage(command);
			connection.receiveMessage();

			// sending TagListFormat
			command = AlienCommandList.TAG_LIST_FORMAT;
			logger.debug("Sening command: " + command);
			connection.sendMessage(command);
			connection.receiveMessage();

			// sending custom format
			command = AlienCommandList.TAG_LIST_CUSTOM_FORMAT;
			logger.debug("Sening command: " + command);
			connection.sendMessage(command);
			connection.receiveMessage();

			while (running) {
				// sending get tag list
				command = AlienCommandList.TAG_LIST;
				logger.debug("Sening command: " + command);
				connection.sendMessage(command);
				String tag_msg = (String) connection.receiveMessage();
				logger.debug("recieved: " + tag_msg);

				List<TagMessage> tagList = this.parseString(tag_msg);

				for (TagMessage m : tagList) {
					messageQueue.addMessage(m);
				}

				try {
					Thread.sleep(poll_period_int);
				} catch (InterruptedException e) {
				}
			}
		} catch (RifidiMessageQueueException e) {
			e.printStackTrace();
			return CommandReturnStatus.INTERRUPTED;
		} catch (IOException e) {
			e.printStackTrace();
			return CommandReturnStatus.INTERRUPTED;
		}
		return CommandReturnStatus.SUCCESSFUL;
	}

	/**
	 * List of Alien commands.
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private static class AlienCommandList {
		/**
		 * Tag list command.
		 */
		public static final String TAG_LIST = ('\1' + "get taglist\n");

		/**
		 * Tag list format command.
		 */
		public static final String TAG_LIST_FORMAT = ('\1' + "set TagListFormat=Custom\n");

		/**
		 * 
		 */
		public static final String TAG_TYPE_COMMAND = ('\1' + "set TagType=");

		/**
		 * 
		 */
		public static final String ANTENNA_SEQUENCE_COMMAND = ('\1' + "set AntennaSequence=");

		public static final String ENDING = "\n";

		/**
		 * Tag list custom format command.
		 */
		public static final String TAG_LIST_CUSTOM_FORMAT = ('\1' + "set TagListCustomFormat=%k|%T|%a\n");

		/**
		 * 
		 */
		public static final String GET_TIME_ZONE = ('\1' + "get TimeZone\n");
	}

	/**
	 * Parses the tag message string.
	 * 
	 * @param input
	 *            The input string, consisting of all of the tag data.
	 * @return A list of TagRead objects parsed from the input string.
	 */
	private List<TagMessage> parseString(String input) {
		String[] splitString = input.split("\n");

		List<TagMessage> retVal = new ArrayList<TagMessage>();

		try {
			for (String s : splitString) {
				s = s.trim();
				String[] splitString2 = s.split("\\|");
				if (splitString2.length > 1) {
					String tagData = splitString2[0];
					String timeStamp = splitString2[1];
					String antennaID = splitString2[2];

					Time time = Time.valueOf(timeStamp);
					calendar.setTime(time);
					Calendar currentDate = Calendar.getInstance(timeZone);
					calendar.set(currentDate.get(Calendar.YEAR), currentDate
							.get(Calendar.MONTH), currentDate
							.get(Calendar.DATE));

					EnhancedTagMessage newTagRead = new EnhancedTagMessage();
					newTagRead.setId(ByteAndHexConvertingUtility
							.fromHexString(tagData.trim()));
					newTagRead.setLastSeenTime(calendar.getTimeInMillis());

					// newTagRead.setVelocity(Float.parseFloat(velocity));

					newTagRead.setAntennaId(Integer.parseInt(antennaID));
					// newTagRead.setSignalStrength(Float.parseFloat(signalStrength));
					retVal.add(newTagRead);

				}else{
					logger.error("Something is invalid: " + splitString2[0]);
				}
			}
		} catch (Exception e) {
			logger.error("There was a problem when parsing Alien Tags.  "
					+ "tag has not been added", e);
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.commands.Command#stop()
	 */
	@Override
	public void stop() {
	}

}
