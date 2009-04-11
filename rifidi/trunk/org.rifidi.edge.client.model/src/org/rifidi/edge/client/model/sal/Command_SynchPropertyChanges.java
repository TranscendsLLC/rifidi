/**
 * 
 */
package org.rifidi.edge.client.model.sal;

import javax.management.AttributeList;

import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_ServerDescription;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_SetReaderProperties;

/**
 * @author kyle
 * 
 */
public class Command_SynchPropertyChanges implements RemoteEdgeServerCommand {

	private RemoteEdgeServer server;

	public Command_SynchPropertyChanges(RemoteEdgeServer server, String readerID) {

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
		// TODO Auto-generated method stub
		return null;
	}

}
