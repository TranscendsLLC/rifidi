/*
 * Command_ReaderFactoryRemoved.java
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

import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.api.jms.notifications.ReaderFactoryRemovedNotification;

/**
 * A command that is executed whenever a ReaderFactory is removed
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class Command_ReaderFactoryRemoved implements RemoteEdgeServerCommand {

	/** An observable map of reader factories */
	private ObservableMap readerFactories;
	/** The ID of the factory that was removed */
	private String readerFactoryID;

	/***
	 * Constructor
	 * 
	 * @param server
	 *            The RemoteEdgeServer instance
	 * @param notification
	 *            The JMS notification that was received
	 */
	public Command_ReaderFactoryRemoved(RemoteEdgeServer server,
			ReaderFactoryRemovedNotification notification) {
		this.readerFactories = server.getReaderFactories();
		readerFactoryID = notification.getReaderFactoryID();

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
		readerFactories.remove(readerFactoryID);
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
		return "READER_FACTORY_REMOVED";
	}

}
