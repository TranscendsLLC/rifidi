package org.rifidi.edge.readerplugin.alien.commands;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.readerplugin.alien.messages.AlienAllInfoMessage;

public class GetAllAlienInfo implements Command {

	private static final Log logger = LogFactory.getLog(GetAllAlienInfo.class);

	@Override
	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, MessageQueue errorQueue,
			CommandConfiguration configuration, long commandID) {
		logger.debug("Starting the " + this.getClass().getSimpleName()
				+ " command for the Alien");
		// running = true;
		try {

			connection.sendMessage("info");
			String message = (String) connection.receiveMessage();

			messageQueue.addMessage(new AlienAllInfoMessage(message));

		} catch (RifidiMessageQueueException e) {
			e.printStackTrace();
			return CommandReturnStatus.INTERRUPTED;
		} catch (IOException e) {
			e.printStackTrace();
			return CommandReturnStatus.INTERRUPTED;
		}
		return CommandReturnStatus.SUCCESSFUL;
	}

	@Override
	public void stop() {

	}

}
