package org.rifidi.edge.readerplugin.alien.commands.general;

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
import org.w3c.dom.Document;


/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
@CommandDesc(name = "Function", groups = {"general"})
public class ReaderType implements Command {
	private static final Log logger = LogFactory
	.getLog(ReaderType.class);

	static private String command = "Function";
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.commands.Command#start(org.rifidi.edge.core.communication.Connection, org.rifidi.edge.core.messageQueue.MessageQueue, java.lang.String, long)
	 */
	@Override
	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, MessageQueue errorQueue,
			Document configuration, long commandID) {
		logger.debug("Starting the " + this.getClass().getSimpleName()
				+ " command for the Alien");
		if (configuration == null || configuration.equals("")) {
			try {
		
				connection.sendMessage("get " + command + "\n");
				String message = (String) connection.receiveMessage();
				
				if (message.contains("=")) {
					String[] temp = message.split("=");
					message = temp[1];
				}
				message = message.trim();
				
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
			return CommandReturnStatus.UNSUCCESSFUL;
		}
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.commands.Command#stop()
	 */
	@Override
	public void stop() {
	
	}
}
