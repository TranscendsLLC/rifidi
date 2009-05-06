/**
 * 
 */
package org.rifidi.edge.client.model.sal;
//TODO: Comments
import javax.management.AttributeList;

import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.core.rmi.client.commandconfigurationstub.CCSetCommandConfigProperties;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;

/**
 * @author kyle
 * 
 */
public class Command_SynchCommandConfigPropertyChanges implements
		RemoteEdgeServerCommand {

	private RemoteEdgeServer server;
	private String commandConfigID;
	private AttributeList attributes;

	/**
	 * Constructor
	 * 
	 * @param server
	 * @param commandConfigID
	 * @param attributes
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
				server.getCCServerDescription(), this.commandConfigID, this.attributes);
		try {
			setProperties.makeCall();
		} catch (ServerUnavailable su) {
			server.disconnect();
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
		// TODO Auto-generated method stub

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
