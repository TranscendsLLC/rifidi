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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.readerplugin.commands.annotations.CommandDesc;
import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;
import org.rifidi.edge.readerplugin.alien.messages.EnhancedTagMessage;
import org.w3c.dom.Document;


/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
@CommandDesc(name = "TagStreamingEnhanced")
public class AlienTagStreamEnhancedCommand implements Command {

	
	private boolean running = false;

	private static final Log logger = LogFactory
			.getLog(AlienTagStreamEnhancedCommand.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.commands.Command#start(org.rifidi.edge.core.communication.Connection,
	 *      org.rifidi.edge.core.messageQueue.MessageQueue)
	 */
	@Override
	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, MessageQueue errorQueue,
			Document configuration, long commandID) {
		logger.debug("Starting the Tag List command for the Alien");
		running = true;
		try {

			connection.sendMessage(AlienCommandList.TAG_LIST_FORMAT);
			connection.receiveMessage();

			connection.sendMessage(AlienCommandList.TAG_LIST_CUSTOM_FORMAT);
			connection.receiveMessage();

			while (running) {

				connection.sendMessage(AlienCommandList.TAG_LIST);
				String tag_msg = (String) connection.receiveMessage();
	
				List<TagMessage> tagList = this.parseString(tag_msg);
	
				for (TagMessage m : tagList) {
					messageQueue.addMessage(m);
				}

				try {
					Thread.sleep(1000);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.commands.Command#stop()
	 */
	@Override
	public void stop() {
		running = false;
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
		 * Tag list custom format command.
		 */
		public static final String TAG_LIST_CUSTOM_FORMAT = ('\1' + "set TagListCustomFormat=%k|%t|%a|%v|%m\n");
	}

	/**
	 * Parses the string.
	 * 
	 * @param input
	 *            The input string, consisting of all of the tag data.
	 * @return A list of TagRead objects parsed from the input string.
	 */
	private List<TagMessage> parseString(String input) {
		String[] splitString = input.split("\n");

		List<TagMessage> retVal = new ArrayList<TagMessage>();

		for (String s : splitString) {
			s = s.trim();
			String[] splitString2 = s.split("\\|");
			if (splitString2.length > 1) {
				String tagData = splitString2[0];
				String timeStamp = splitString2[1];
				String antennaID = splitString2[2];
				String velocity = splitString2[3];
				String signalStrength = splitString2[4];
				
				Time time = Time.valueOf(timeStamp);

				EnhancedTagMessage newTagRead = new EnhancedTagMessage();
				newTagRead.setId(ByteAndHexConvertingUtility
						.fromHexString(tagData.trim()));
				newTagRead.setLastSeenTime(time.getTime());
				newTagRead.setVelocity(Float.parseFloat(velocity));
				
				newTagRead.setAntennaId(Integer.parseInt(antennaID));
				newTagRead.setSignalStrength(Float.parseFloat(signalStrength));
				retVal.add(newTagRead);

			}
		}
		return retVal;
	}

	
}
