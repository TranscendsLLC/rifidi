package org.rifidi.edge.readerplugin.dummy.properties;

import java.io.IOException;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;
import org.w3c.dom.Document;

public class GetTagsOnceCommandBroken implements Command {
	private static final Log logger = LogFactory
			.getLog(GetTagsOnceCommandBroken.class);
	boolean running = true;

	Random random = new Random();

	@Override
	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, MessageQueue errorQueue,
			Document configuration, long commandID) {
		logger.debug("Getting tags.");
	
		String rawtag = ByteAndHexConvertingUtility.toHexString(
				"Hallo".getBytes()).replace(" ", "")
				+ "|" + 1565467895l + "\n\n";
		try {
			connection.sendMessage(rawtag);
			rawtag = (String) connection.receiveMessage();
		} catch (IOException e1) {
			return CommandReturnStatus.INTERRUPTED;
		}

		if (random.nextDouble() <= 0)
			return CommandReturnStatus.INTERRUPTED;

		if (!rawtag.equals("")) {
			String[] rawTags = rawtag.split("\n");
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
