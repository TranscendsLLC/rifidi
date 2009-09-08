/*
 * Command_SynchReaderPropertyChanges.java
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

import javax.management.AttributeList;

import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_SetReaderProperties;
import org.rifidi.rmi.proxycache.exceptions.AuthenticationException;
import org.rifidi.rmi.proxycache.exceptions.ServerUnavailable;

/**
 * A command that commits property changes to a reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Command_SynchReaderPropertyChanges implements
		RemoteEdgeServerCommand {

	/** The edge server model object */
	private RemoteEdgeServer server;
	/** The ID of the reader */
	private String readerID;
	/** The attributes that have changed */
	private AttributeList attributes;

	/**
	 * Constructor
	 * 
	 * @param server
	 *            The Edge server model object
	 * @param readerID
	 *            The ID of the reader
	 * @param attributes
	 *            The attributes that have changed
	 */
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
		return "COMMAND_SYNCH_READER_PROPERTY_CHANGES";
	}

}
