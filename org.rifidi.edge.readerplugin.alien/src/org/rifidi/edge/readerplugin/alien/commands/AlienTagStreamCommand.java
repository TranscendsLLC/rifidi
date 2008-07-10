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

import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.annotations.CommandDesc;
import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;
import org.rifidi.edge.core.readersession.impl.CommandStatus;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
@CommandDesc(name = "getTagList")
public class AlienTagStreamCommand implements Command {

	private boolean running = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.commands.Command#start(org.rifidi.edge.core.communication.Connection,
	 *      org.rifidi.edge.core.messageQueue.MessageQueue)
	 */
	@Override
	public CommandStatus start(Connection connection, MessageQueue messageQueue)
			throws IOException {
		running = true;
		try {

			connection.sendMessage(AlienCommandList.TAG_LIST_FORMAT);
			connection.recieveMessage();

			connection.sendMessage(AlienCommandList.TAG_LIST_CUSTOM_FORMAT);
			connection.recieveMessage();

			while (running) {
				connection.sendMessage(AlienCommandList.TAG_LIST);
				String tag_msg = (String) connection.recieveMessage();

				List<TagMessage> tagList = this.parseString(tag_msg);

				for (TagMessage m : tagList) {
					messageQueue.addMessage(m);
				}
			}
		} catch (RifidiMessageQueueException e) {
			throw new IOException(e);
		}
		return CommandStatus.SUCCESSFUL;
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
		public static final String TAG_LIST_CUSTOM_FORMAT = ('\1' + "set TagListCustomFormat=%k|%t\n");
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

				Time time = Time.valueOf(timeStamp);

				TagMessage newTagRead = new TagMessage();
				newTagRead.setId(ByteAndHexConvertingUtility
						.fromHexString(tagData.trim()));
				newTagRead.setLastSeenTime(time.getTime());
				retVal.add(newTagRead);

			}
		}
		return retVal;
	}
}
