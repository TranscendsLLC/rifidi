package org.rifidi.edge.readerplugin.thingmagic.commands;

import java.io.IOException;

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

@CommandDesc(name = "getTagList")
public class GetTagsOnceCommand implements Command {
	private static final Log logger = LogFactory
			.getLog(GetTagsOnceCommand.class);
	private static final String GET_TAGS = "select id, timestamp from tag_id;";

	@Override
	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, String configuration, long commandID) {
		try {
			logger.debug("Issuing Command: " + GET_TAGS);
			connection.sendMessage(GET_TAGS);
		} catch (IOException e) {
			return CommandReturnStatus.INTERRUPTED;
		}

		String recieved;
		try {
			recieved = (String) connection.receiveMessage();
		} catch (IOException e1) {
			return CommandReturnStatus.INTERRUPTED;
		}

		logger.debug("Returned: " + recieved);
		if (!recieved.equals("")) {
			String[] rawTags = recieved.split("\n");
			for (String rawTag : rawTags) {
				// logger.debug(rawTag);

				// All tag data sent back is separated by vertical bars.
				String[] rawTagItems = rawTag.split("\\|");

				TagMessage tag = new TagMessage();

				// logger.debug(rawTagItems[0]);

				tag.setId(ByteAndHexConvertingUtility
						.fromHexString(rawTagItems[0].substring(2,
								rawTagItems[0].length())));

				// TODO: correct the time stamps.
				tag.setLastSeenTime(System.nanoTime());
				// logger.debug(tag.toXML());
				try {
					messageQueue.addMessage(tag);
				} catch (RifidiMessageQueueException e) {
					return CommandReturnStatus.INTERRUPTED;
				}
			}

		}
		return CommandReturnStatus.SUCCESSFUL;
	}

	@Override
	public void stop() {
		// Do Nothing

	}

}
