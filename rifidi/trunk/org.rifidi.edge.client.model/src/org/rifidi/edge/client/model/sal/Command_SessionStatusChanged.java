/*
 * Command_SessionStatusChanged.java
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

import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.api.jms.notifications.SessionStatusChangedNotification;

/**
 * A command that is executed when the status of a session changes
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Command_SessionStatusChanged implements RemoteEdgeServerCommand {

	/** The ID of the reader that the session belongs to */
	private String readerID;
	/** The ID of the session whose status changed */
	private String sessionID;
	/** The new status */
	private SessionStatus sessionStatus;
	/** The remote edge server */
	private RemoteEdgeServer server;

	/**
	 * Constructor
	 * 
	 * @param server
	 *            The remote edge server instance
	 * @param notification
	 *            The JMS notification that was received
	 */
	public Command_SessionStatusChanged(RemoteEdgeServer server,
			SessionStatusChangedNotification notification) {
		this.readerID = notification.getReaderID();
		this.sessionID = notification.getSessionID();
		this.sessionStatus = notification.getStatus();
		this.server = server;
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
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand#
	 * executeEclipse()
	 */
	@Override
	public void executeEclipse() {
		RemoteReader reader = (RemoteReader) server.getRemoteReaders().get(
				this.readerID);
		if (reader != null) {
			RemoteSession session = (RemoteSession) reader.getRemoteSessions()
					.get(this.sessionID);
			if (session != null) {
				session.setStatus(this.sessionStatus);
			}
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
		return "SESSION_STATUS_CHANGED";
	}

}
