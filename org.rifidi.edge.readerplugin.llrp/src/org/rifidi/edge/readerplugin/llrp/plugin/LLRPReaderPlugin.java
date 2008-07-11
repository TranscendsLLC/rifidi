/**
 * 
 */
package org.rifidi.edge.readerplugin.llrp.plugin;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.protocol.MessageProtocol;
import org.rifidi.edge.readerplugin.llrp.commands.LLRPTagStreamCommand;
import org.rifidi.edge.readerplugin.llrp.protocol.LLRPMessageProtocol;

/**
 * @author Kyle Neumeier kyle@pramari.com
 *
 */
public class LLRPReaderPlugin implements ReaderPlugin {

	private ArrayList<Class<? extends Command>> commands = new ArrayList<Class<? extends Command>>();
	private LLRPConnectionManager connectionManager;
	private MessageProtocol messageProtocol;
	
	public LLRPReaderPlugin(){
		commands.add(LLRPTagStreamCommand.class);
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#addCommand(java.util.List)
	 */
	@Override
	public void addCommand(List<Class<? extends Command>> commands) {
		commands.addAll(commands);

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getAvailableCommands()
	 */
	@Override
	public List<Class<? extends Command>> getAvailableCommands() {
		return new ArrayList<Class<? extends Command>>(commands);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getConnectionManager(org.rifidi.edge.core.readerplugin.ReaderInfo)
	 */
	@Override
	public ConnectionManager getConnectionManager(ReaderInfo readerInfo) {
		if(connectionManager==null){
			connectionManager = new LLRPConnectionManager(readerInfo);
		}
		return connectionManager;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getMessageProtocol()
	 */
	@Override
	public MessageProtocol getMessageProtocol() {
		if(messageProtocol==null){
			messageProtocol = new LLRPMessageProtocol();
		}
		return messageProtocol;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#removeCommand(java.util.List)
	 */
	@Override
	public void removeCommand(List<Class<? extends Command>> commands) {
		for(Class<? extends Command> c : commands){
			this.commands.remove(c);
		}

	}

}
