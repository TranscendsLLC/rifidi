package org.rifidi.edge.core.readerplugin;

import java.util.List;

import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.protocol.MessageProtocol;

/**
 * ReaderPlugin is a collection of Commands a certain type of reader provides.
 * It's also used as a factory to create instances of necessary parts of this
 * special ReaderPlugin. All ReaderPlugins need to implement this Interface.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
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
