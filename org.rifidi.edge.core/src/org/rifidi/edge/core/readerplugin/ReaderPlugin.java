package org.rifidi.edge.core.readerplugin;

import java.util.List;

import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.protocol.MessageProtocol;

public interface ReaderPlugin {

	public ConnectionManager getConnectionManager();

	public List<Class<? extends Command>> getAvailableCommands();

	public void addCommand(List<Class<? extends Command>> commands);

	public MessageProtocol getMessageProtocol();
}
