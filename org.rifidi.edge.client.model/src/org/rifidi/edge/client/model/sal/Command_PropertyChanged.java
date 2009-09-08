/*
 * Command_PropertyChanged.java
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

import javax.management.Attribute;

import org.rifidi.edge.api.jms.notifications.PropertyChangedNotification;
import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;

/**
 * This command is executed when there was a property changed event (a property
 * either of a Reader or a CommandConfiguration)
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class Command_PropertyChanged implements RemoteEdgeServerCommand {

	private RemoteEdgeServer server;
	private PropertyChangedNotification notification;

	public Command_PropertyChanged(RemoteEdgeServer server,
			PropertyChangedNotification notification) {
		this.server = server;
		this.notification = notification;
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
		// DO NOTHING

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand#
	 * executeEclipse()
	 */
	@Override
	public void executeEclipse() {
		if (server.remoteReaders.get(notification.getConfigID()) != null) {
			RemoteReader reader = (RemoteReader) server.remoteReaders
					.get(notification.getConfigID());
			if (reader != null) {
				for (Attribute a : notification.getAttributes().asList()) {
					reader.setAttribute(a);
				}
			}

		} else if (server.commandConfigurations.get(notification.getConfigID()) != null) {
			RemoteCommandConfiguration config = (RemoteCommandConfiguration) server.commandConfigurations
					.get(notification.getConfigID());
			if (config != null) {
				for (Attribute a : notification.getAttributes().asList()) {
					config.setAttribute(a);
				}
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
		return "PROPERTY_CHANGED";
	}

}
