package org.rifidi.edge.core.readerplugin.commands;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;

/**
 * This is the command Interface defining a common base over all Commands
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface Command {

	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, String configuration, long commandID);

	public void stop();
}
