package org.rifidi.edge.readerplugin.dummy;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.protocol.MessageProtocol;
import org.rifidi.edge.readerplugin.dummy.commands.TagStreamCommand;
import org.rifidi.edge.readerplugin.dummy.protocol.DummyMessageProtocol;

public class DummyReaderPlugin implements ReaderPlugin {

	@Override
	public List<Class<? extends Command>> getAvailableCommands() {
		ArrayList<Class<? extends Command>> commands = new ArrayList<Class<? extends Command>>();
		commands.add(TagStreamCommand.class);
		return commands;
	}

	@Override
	public MessageProtocol getMessageProtocol() {
		return new DummyMessageProtocol();
	}

	//FIXME implement ConnectionManager
	@Override
	public ConnectionManager getConnectionManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addCommand(List<Class<? extends Command>> commands) {
		// TODO Auto-generated method stub
		
	}

}
