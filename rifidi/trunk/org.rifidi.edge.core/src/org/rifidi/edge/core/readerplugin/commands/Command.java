package org.rifidi.edge.core.readerplugin.commands;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;

/**
 * This is the command Interface defining a common base over all Commands
 * 
 * @author andreas
 * 
 */
public interface Command {

	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, String configuration, long commandID);

	public void stop();
}
