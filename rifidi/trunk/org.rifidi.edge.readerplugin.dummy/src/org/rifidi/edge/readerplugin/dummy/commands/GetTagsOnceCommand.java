package org.rifidi.edge.readerplugin.dummy.commands;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.annotations.CommandDesc;
import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;

@CommandDesc(name="GetTagsCurrentlyOnAntennas")
public class GetTagsOnceCommand implements Command {
	private static final Log logger = LogFactory.getLog(GetTagsOnceCommand.class);
	boolean running = true;
	private MessageQueue queue;
	
	@Override
	public void start(Connection connection, MessageQueue messageQueue) throws IOException {
		logger.debug("Getting tags.");
		this.queue = messageQueue;
		TagMessage message = new TagMessage();
		message.setId("Hallo".getBytes());
		message.setLastSeenTime(1565467895l);

		try {
			queue.addMessage(message);
		} catch (RifidiMessageQueueException e) {
			throw new IOException(e);
		}
		

	}

	@Override
	public void stop() {
		// Do Nothing

	}

}
