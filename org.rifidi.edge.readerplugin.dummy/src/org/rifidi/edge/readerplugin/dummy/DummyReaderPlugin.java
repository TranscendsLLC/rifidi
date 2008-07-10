package org.rifidi.edge.readerplugin.dummy;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.protocol.MessageProtocol;
import org.rifidi.edge.readerplugin.dummy.commands.GetTagsOnceCommand;
import org.rifidi.edge.readerplugin.dummy.commands.TagStreamCommand;
import org.rifidi.edge.readerplugin.dummy.protocol.DummyMessageProtocol;

public class DummyReaderPlugin implements ReaderPlugin {
	ArrayList<Class<? extends Command>> commands = new ArrayList<Class<? extends Command>>();
	
	public DummyReaderPlugin(){		
		commands.add(TagStreamCommand.class);
		commands.add(GetTagsOnceCommand.class);
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getAvailableCommands()
	 */
	@Override
	public List<Class<? extends Command>> getAvailableCommands() {
		return commands;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getMessageProtocol()
	 */
	@Override
	public MessageProtocol getMessageProtocol() {
		return new DummyMessageProtocol();
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#addCommand(java.util.List)
	 */
	@Override
	public void addCommand(List<Class<? extends Command>> commands) {
		commands.addAll(commands);
		
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getConnectionManager(org.rifidi.edge.core.readerplugin.ReaderInfo)
	 */
	@Override
	public ConnectionManager getConnectionManager(ReaderInfo readerInfo) {
		return new DummyConnectionManager(readerInfo);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#removeCommand(java.util.List)
	 */
	@Override
	public void removeCommand(List<Class<? extends Command>> commands) {
		commands.removeAll(commands);		
	}

}
