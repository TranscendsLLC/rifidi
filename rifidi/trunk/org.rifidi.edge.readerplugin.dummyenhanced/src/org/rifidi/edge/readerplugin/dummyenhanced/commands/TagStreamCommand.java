package org.rifidi.edge.readerplugin.dummyenhanced.commands;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.readerplugin.messages.impl.EnhancedTagMessage;
import org.w3c.dom.Document;

public class TagStreamCommand implements Command {

	boolean running = true;

	Log logger = LogFactory.getLog(TagStreamCommand.class);

	@Override
	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, MessageQueue errorQueue,
			Document configuration, long commandID) {

		logger.debug("TagStreaming is running!");
		while (running) {
			String rawtag = "";
			try {
				connection.sendMessage("tag");
				rawtag = (String) connection.receiveMessage();
				logger.debug(rawtag);
			} catch (IOException e1) {
				return CommandReturnStatus.INTERRUPTED;
			}

			logger.debug("inspecting tag list");
			if (!rawtag.equals("")) {
				String[] rawTags = rawtag.split("\n");
				for (String rawTag : rawTags) {
					logger.debug(rawTag);

					// All tag data sent back is separated by vertical bars.
					String[] rawTagItems = rawTag.split("\\|");

					EnhancedTagMessage tag = new EnhancedTagMessage();

					// logger.debug(rawTagItems[0]);

					tag.setId(ByteAndHexConvertingUtility
							.fromHexString(rawTagItems[0].substring(2,
									rawTagItems[0].length())));

					tag.setLastSeenTime(Long.parseLong(rawTagItems[1]));

					tag.setAntennaId(Integer.parseInt(rawTagItems[2]));

					tag.setVelocity(Float.parseFloat(rawTagItems[3]));

					tag.setSignalStrength(Float.parseFloat(rawTagItems[4]));
					try {
						messageQueue.addMessage(tag);
					} catch (RifidiMessageQueueException e) {
						return CommandReturnStatus.INTERRUPTED;
					}
				}

			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		logger.debug("TagStreaming is stopped");
		return CommandReturnStatus.SUCCESSFUL;
	}

	@Override
	public void stop() {
		running = false;
	}

}
