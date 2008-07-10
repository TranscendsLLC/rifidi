package org.rifidi.edge.core.readerplugin.commands;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;

public interface Command {

	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue);

	public void stop();
}
