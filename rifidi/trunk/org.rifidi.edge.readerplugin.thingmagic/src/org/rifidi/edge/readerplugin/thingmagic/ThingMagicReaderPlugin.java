package org.rifidi.edge.readerplugin.thingmagic;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.protocol.MessageProtocol;
import org.rifidi.edge.readerplugin.thingmagic.commands.GetTagsOnceCommand;
import org.rifidi.edge.readerplugin.thingmagic.commands.TagStreamCommand;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class ThingMagicReaderPlugin implements ReaderPlugin {
	ArrayList<Class<? extends Command>> commands = new ArrayList<Class<? extends Command>>();
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getAvailableCommands()
	 */
	@Override
	public List<Class<? extends Command>> getAvailableCommands() {
		commands.add(TagStreamCommand.class);
		commands.add(GetTagsOnceCommand.class);
		return commands;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getConnectionManager()
	 */
	@Override
	public ConnectionManager getConnectionManager() {
		// TODO Auto-generated method stub
		return new ThingMagicManager(null);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getMessageProtocol()
	 */
	@Override
	public MessageProtocol getMessageProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addCommand(List<Class<? extends Command>> commands) {
		commands.addAll(commands);
	}

}
