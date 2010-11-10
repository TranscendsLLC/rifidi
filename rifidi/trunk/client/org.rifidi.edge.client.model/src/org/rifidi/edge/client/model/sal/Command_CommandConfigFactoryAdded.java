/*
 * Command_CommandConfigFactoryAdded.java
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

import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.rifidi.edge.api.jms.notifications.CommandConfigFactoryAdded;
import org.rifidi.edge.api.rmi.dto.CommandConfigFactoryDTO;
import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.client.model.sal.commands.RequestExecuterSingleton;
import org.rifidi.edge.core.rmi.client.commandconfigurationstub.CCGetCommandConfigDescription;
import org.rifidi.edge.core.rmi.client.commandconfigurationstub.CCGetCommandConfigFactory;
import org.rifidi.edge.core.rmi.client.commandconfigurationstub.CCServerDescription;
import org.rifidi.rmi.proxycache.exceptions.AuthenticationException;
import org.rifidi.rmi.proxycache.exceptions.ServerUnavailable;

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
	private String commandFactoryID;
	/** The DTO of the CommandConfigFactori */
	private CommandConfigFactoryDTO dto;
	/** The server description of the RMI Command stub */
	private CCServerDescription serverDescription;
	/** A command to run if there is a problem */
	private Command_Disconnect disconnectCommand;
	/** The logger for this class */
	private Log logger = LogFactory
			.getLog(Command_CommandConfigFactoryAdded.class);
	/** The meta information about the properties for this factory */
	private MBeanInfo mbeanInfo;

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
		this.commandFactoryID = notification.getCommandFactoryID();
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
				serverDescription, commandFactoryID);

		try {
			dto = getFactory.makeCall();
			CCGetCommandConfigDescription getDescription = new CCGetCommandConfigDescription(
					serverDescription, commandFactoryID);
			MBeanInfo info = getDescription.makeCall();
			if (info == null) {
				logger.warn("Info for " + commandFactoryID + " is null!");
			}
			this.mbeanInfo = info;
		} catch (ServerUnavailable e) {
			logger.error("Error while getting Command Factory: ", e);
			RequestExecuterSingleton.getInstance().scheduleRequest(
					disconnectCommand);
		} catch (AuthenticationException e) {
			logger.warn("Authentication Exception ", e);
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
			commandConfigFactories.put(commandFactoryID,
					new RemoteCommandConfigFactory(dto, mbeanInfo));
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
