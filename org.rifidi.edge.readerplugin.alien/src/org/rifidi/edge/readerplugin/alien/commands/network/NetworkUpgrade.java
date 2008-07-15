package org.rifidi.edge.readerplugin.alien.commands.network;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.readerplugin.commands.annotations.CommandDesc;
import org.rifidi.edge.readerplugin.alien.messages.GenericAlienMessage;
import org.rifidi.edge.readerplugin.alien.messages.Property;

@CommandDesc(name = "NetworkUpgrade", groups = {"network"})
public class NetworkUpgrade implements Command {
	private static final Log logger = LogFactory
			.getLog(NetworkUpgrade.class);

	static private String command = "NetworkUpgrade";

	@Override
	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, String configuration, long commandID) {
		logger.debug("Starting the " + this.getClass().getSimpleName()
				+ " command for the Alien");
		if (configuration == null) {
			try {

				connection.sendMessage("get " + command + "\n");
				String message = (String) connection.recieveMessage();

				messageQueue.addMessage(new GenericAlienMessage(new Property(
						command, message)));

			} catch (RifidiMessageQueueException e) {
				e.printStackTrace();
				return CommandReturnStatus.INTERRUPTED;
			} catch (IOException e) {
				e.printStackTrace();
				return CommandReturnStatus.INTERRUPTED;
			}
			return CommandReturnStatus.SUCCESSFUL;
		} else {
			try {

				connection.sendMessage("set " + command + "=" + configuration
						+ "\n");
				String message = (String) connection.recieveMessage();

				messageQueue.addMessage(new GenericAlienMessage(new Property(
						command, message)));

			} catch (RifidiMessageQueueException e) {
				e.printStackTrace();
				return CommandReturnStatus.INTERRUPTED;
			} catch (IOException e) {
				e.printStackTrace();
				return CommandReturnStatus.INTERRUPTED;
			}
			return CommandReturnStatus.SUCCESSFUL;
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
