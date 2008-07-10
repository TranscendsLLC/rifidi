package org.rifidi.edge.core.readerplugin.commands;

import java.io.IOException;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;

public interface Command {

	public CommandReturnStatus start(Connection connection, MessageQueue messageQueue) throws IOException;

	public void stop();
}
