
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
