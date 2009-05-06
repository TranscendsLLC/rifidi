/**
 * 
 */
package org.rifidi.edge.client.model.sal;
//TODO: Comments
import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.core.api.jms.notifications.CommandConfigurationRemovedNotification;

/**
 * This is a command that is executed when a CommandConfiguration is removed
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Command_CommandConfigurationRemoved implements
		RemoteEdgeServerCommand {

	/** The commandConfigurations */
	private ObservableMap commandConfigurations;
	/** The ID of the commandConfiguration */
	private String commandConfigurationID;

	public Command_CommandConfigurationRemoved(RemoteEdgeServer server,
			CommandConfigurationRemovedNotification notification) {
		this.commandConfigurations = server.commandConfigurations;
		this.commandConfigurationID = notification.getCommandConfigurationID();
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
		// Do Nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand#
	 * executeEclipse()
	 */
	@Override
	public void executeEclipse() {
		commandConfigurations.remove(commandConfigurationID);
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
		return "COMMAND_CONFIGURATION_REMOVED";
	}

}
