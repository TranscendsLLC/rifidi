package org.rifidi.edge.readerplugin.thingmagic;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.protocol.MessageProtocol;
import org.rifidi.edge.readerplugin.thingmagic.commands.TagStreamCommand;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class ThingMagicReaderPlugin implements ReaderPlugin {

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getAvailableCommands()
	 */
	@Override
	public List<Class<? extends Command>> getAvailableCommands() {
		ArrayList<Class<? extends Command>> commands = new ArrayList<Class<? extends Command>>();
		commands.add(TagStreamCommand.class);
		return commands;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getConnectionManager()
	 */
	@Override
	public ConnectionManager getConnectionManager() {
		// TODO Auto-generated method stub
		return new ThingMagicManager();
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getMessageProtocol()
	 */
	@Override
	public MessageProtocol getMessageProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

}
