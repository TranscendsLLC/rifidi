/**
 * 
 */
package org.rifidi.edge.client.model.sal;

import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.core.api.jms.notifications.CommandConfigFactoryRemoved;

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
	private String readerFactoryID;

	public Command_CommandConfigFactoryRemoved(RemoteEdgeServer server,
			CommandConfigFactoryRemoved notification) {
		this.commandConfigFactories = server.commandConfigFactories;
		this.readerFactoryID = notification.getReaderFactoryID();
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
		commandConfigFactories.remove(readerFactoryID);

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
