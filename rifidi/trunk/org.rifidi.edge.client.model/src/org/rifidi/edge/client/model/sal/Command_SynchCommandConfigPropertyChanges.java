package org.rifidi.edge.client.model.sal;

import javax.management.AttributeList;

import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.core.rmi.client.commandconfigurationstub.CCSetCommandConfigProperties;
import org.rifidi.rmi.proxycache.exceptions.AuthenticationException;
import org.rifidi.rmi.proxycache.exceptions.ServerUnavailable;

/**
 * A command that commits property changes to a CommandConfiguration
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class Command_SynchCommandConfigPropertyChanges implements
		RemoteEdgeServerCommand {

	/** The Remote Edge Server */
	private RemoteEdgeServer server;
	/** The ID of the command configuraiton */
	private String commandConfigID;
	/** The attributes that have changed */
	private AttributeList attributes;

	/**
	 * Constructor
	 * 
	 * @param server
	 *            The Edge Server model object
	 * @param commandConfigID
	 *            The ID of the command configuration
	 * @param attributes
	 *            The attributes that have changed
	 */
	public Command_SynchCommandConfigPropertyChanges(RemoteEdgeServer server,
			String commandConfigID, AttributeList attributes) {
		super();
		this.server = server;
		this.commandConfigID = commandConfigID;
		this.attributes = attributes;
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
		CCSetCommandConfigProperties setProperties = new CCSetCommandConfigProperties(
				server.getCCServerDescription(), this.commandConfigID,
				this.attributes);
		try {
			setProperties.makeCall();
		} catch (ServerUnavailable su) {
			server.disconnect();
		} catch (AuthenticationException e) {
			server.handleAuthenticationException(e);
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
		// DO NOTHING
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
		return "COMMAND_SYNCH_COMMAND_CONFIG_PROPERTY_CHANGES";
	}

}
