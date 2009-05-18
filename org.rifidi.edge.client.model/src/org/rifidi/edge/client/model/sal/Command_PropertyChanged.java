
package org.rifidi.edge.client.model.sal;

import javax.management.Attribute;

import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.core.api.jms.notifications.PropertyChangedNotification;

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
		if (notification.isReader()) {
			RemoteReader reader = (RemoteReader) server.remoteReaders
					.get(notification.getConfigIDID());
			if (reader != null) {
				for (Attribute a : notification.getAttributes().asList()) {
					reader.setAttribute(a);
				}
			}
		} else {
			RemoteCommandConfiguration config = (RemoteCommandConfiguration) server.commandConfigurations
					.get(notification.getConfigIDID());
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
