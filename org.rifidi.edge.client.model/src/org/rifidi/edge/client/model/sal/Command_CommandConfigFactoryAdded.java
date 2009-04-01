/**
 * 
 */
package org.rifidi.edge.client.model.sal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.client.model.sal.commands.RequestExecuterSingleton;
import org.rifidi.edge.core.api.jms.notifications.CommandConfigFactoryAdded;
import org.rifidi.edge.core.api.rmi.dto.CommandConfigFactoryDTO;
import org.rifidi.edge.core.rmi.client.commandconfigurationstub.CCGetCommandConfigFactory;
import org.rifidi.edge.core.rmi.client.commandconfigurationstub.CCServerDescription;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;

/**
 * A Command that is executed when a command configuration factory has been
 * added
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Command_CommandConfigFactoryAdded implements
		RemoteEdgeServerCommand {

	/** The map of command configuraiton factories */
	private ObservableMap commandConfigFactories;
	/** The ID of the readerfactory the new commandconfigFactory works with */
	private String readerFactoryID;
	/** The DTO of the CommandConfigFactori */
	private CommandConfigFactoryDTO dto;
	/** The server description of the RMI Command stub */
	private CCServerDescription serverDescription;
	/** A command to run if there is a problem */
	private Command_Disconnect disconnectCommand;
	/** The logger for this class */
	private Log logger = LogFactory
			.getLog(Command_CommandConfigFactoryAdded.class);

	/**
	 * Constructor
	 * 
	 * @param server
	 *            The RemoteEdgeServer
	 * @param notification
	 *            The notification that was recieved
	 */
	public Command_CommandConfigFactoryAdded(RemoteEdgeServer server,
			CommandConfigFactoryAdded notification) {
		this.commandConfigFactories = server.commandConfigFactories;
		this.readerFactoryID = notification.getReaderFactoryID();
		this.serverDescription = server.getCCServerDescription();
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
		CCGetCommandConfigFactory getFactory = new CCGetCommandConfigFactory(
				serverDescription, readerFactoryID);
		try {
			dto = getFactory.makeCall();
		} catch (ServerUnavailable e) {
			logger.error("Error while getting Command Factory: ", e);
			RequestExecuterSingleton.getInstance().scheduleRequest(
					disconnectCommand);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand#
	 * executeEclipse()
	 */
	@Override
	public void executeEclipse() {
		if (dto != null) {
			commandConfigFactories.put(readerFactoryID,
					new RemoteCommandConfigFactory(dto));
		}

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
		return "COMMAND_CONFIG_FACTORY_ADDED";
	}

}
