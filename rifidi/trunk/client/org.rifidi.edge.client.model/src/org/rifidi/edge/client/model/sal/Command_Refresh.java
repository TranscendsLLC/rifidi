/*
 * Command_Refresh.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */

package org.rifidi.edge.client.model.sal;

import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;

/**
 * A command that is executed to refresh the connection
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class Command_Refresh implements RemoteEdgeServerCommand {

	/** The server to refresh */
	private RemoteEdgeServer server;
	/** A command to disconnect */
	private Command_Disconnect disconnectCommand;

	/***
	 * Constructor
	 * 
	 * @param server
	 *            The server to refresh
	 */
	public Command_Refresh(RemoteEdgeServer server) {
		this.server = server;
		this.disconnectCommand = new Command_Disconnect(server);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand#execute
	 * ()
	 */
	@Override
	public void execute() {
		// first run the disconnect that runs outside of the eclipse thread
		disconnectCommand.execute();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand#
	 * executeEclipse()
	 */
	@Override
	public void executeEclipse() {
		// next run the disconnect that runs inside of the eclipse thread
		disconnectCommand.executeEclipse();
		// finally reconnect
		server.connect();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand#getType
	 * ()
	 */
	@Override
	public String getType() {
		return "REFRESH";
	}

}
