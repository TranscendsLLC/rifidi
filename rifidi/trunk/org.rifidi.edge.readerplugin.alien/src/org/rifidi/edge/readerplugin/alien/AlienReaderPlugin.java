/* 
 * AlienReaderPlugin.java
 *  Created:	Jul 10, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.alien;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.protocol.MessageProtocol;
import org.rifidi.edge.readerplugin.alien.protocol.AlienMessageProtocol;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class AlienReaderPlugin implements ReaderPlugin {

	private List<Class<? extends Command>> commands;

	/**
	 * 
	 */
	public AlienReaderPlugin() {
		commands = new ArrayList<Class<? extends Command>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getAvailableCommands()
	 */
	@Override
	public List<Class<? extends Command>> getAvailableCommands() {
		return commands;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getMessageProtocol()
	 */
	@Override
	public MessageProtocol getMessageProtocol() {
		return new AlienMessageProtocol();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#addCommand(java.util.List)
	 */
	@Override
	public void addCommand(List<Class<? extends Command>> commands) {
		commands.addAll(commands);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getConnectionManager(org.rifidi.edge.core.readerplugin.ReaderInfo)
	 */
	@Override
	public ConnectionManager getConnectionManager(ReaderInfo readerInfo) {
		return new AlienConnectionManager(readerInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#removeCommand(java.util.List)
	 */
	@Override
	public void removeCommand(List<Class<? extends Command>> commands) {
		commands.removeAll(commands);
	}
}
