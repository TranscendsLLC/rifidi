package org.rifidi.edge.core.readerplugin;

import java.util.List;

import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.protocol.MessageProtocol;

public interface ReaderPlugin {

	public ConnectionManager getConnectionManager(ReaderInfo readerInfo);

	public List<Class<? extends Command>> getAvailableCommands();

	// TODO Need also remove command.
	// TODO Consider making this abstract class and implementing this for all
	// readers.
	public void addCommand(List<Class<? extends Command>> commands);

	public void removeCommand(List<Class<? extends Command>> commands);

	public MessageProtocol getMessageProtocol();
}
