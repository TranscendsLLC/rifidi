package org.rifidi.edge.core.readerplugin.commands;

import java.io.IOException;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readersession.impl.CommandStatus;

public interface Command {

	public CommandStatus start(Connection connection, MessageQueue messageQueue) throws IOException;

	public void stop();
}
