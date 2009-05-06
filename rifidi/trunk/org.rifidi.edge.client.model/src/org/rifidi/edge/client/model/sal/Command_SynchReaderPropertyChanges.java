/**
 * 
 */
package org.rifidi.edge.client.model.sal;
//TODO: Comments
import javax.management.AttributeList;

import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_SetReaderProperties;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;

/**
 * @author kyle
 * 
 */
public class Command_SynchReaderPropertyChanges implements RemoteEdgeServerCommand {

	private RemoteEdgeServer server;
	private String readerID;
	private AttributeList attributes;

	public Command_SynchReaderPropertyChanges(RemoteEdgeServer server,
			String readerID, AttributeList attributes) {
		this.server = server;
		this.readerID = readerID;
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

		RS_SetReaderProperties setProperties = new RS_SetReaderProperties(
				server.getRSServerDescription(), readerID, attributes);
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
		// TODO Auto-generated method stub
		return "COMMAND_SYNCH_READER_PROPERTY_CHANGES";
	}

}
