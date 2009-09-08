/*
 * Command_CommandConfigFactoryRemoved.java
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

import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.api.jms.notifications.CommandConfigFactoryRemoved;

/**
 * This command removes a command configuration factory from the remote edge
 * server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Command_CommandConfigFactoryRemoved implements
		RemoteEdgeServerCommand {

	/** The commandConfigurationFactories */
	private ObservableMap commandConfigFactories;
	/** The ID of the readerFactory that the command factory belongs to */
	private String commandFactoryID;

	public Command_CommandConfigFactoryRemoved(RemoteEdgeServer server,
			CommandConfigFactoryRemoved notification) {
		this.commandConfigFactories = server.commandConfigFactories;
		this.commandFactoryID = notification.getCommandFactoryID();
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
		// Do nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand#
	 * executeEclipse()
	 */
	@Override
	public void executeEclipse() {
		commandConfigFactories.remove(commandFactoryID);

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
		return "COMMAND_CONFIG_FACTORY_REMOVED";
	}

}
