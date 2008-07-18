package org.rifidi.edge.readerplugin.thingmagic.plugin;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.readerplugin.ReaderInfo;
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

	public ThingMagicReaderPlugin(){
		commands.add(TagStreamCommand.class);
		commands.add(GetTagsOnceCommand.class);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getAvailableCommands()
	 */
	@Override
	public List<Class<? extends Command>> getAvailableCommands() {
		return new ArrayList<Class<? extends Command>>(commands);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getConnectionManager()
	 */
	@Override
	public ConnectionManager getConnectionManager(ReaderInfo readerInfo) {
		return new ThingMagicManager(readerInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getMessageProtocol()
	 */
	@Override
	public MessageProtocol getMessageProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#addCommand(java.util.List)
	 */
	@Override
	public void addCommand(List<Class<? extends Command>> commands) {
		commands.addAll(commands);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#removeCommand(java.util.List)
	 */
	@Override
	public void removeCommand(List<Class<? extends Command>> commands) {
		commands.removeAll(commands);	
	}

}
