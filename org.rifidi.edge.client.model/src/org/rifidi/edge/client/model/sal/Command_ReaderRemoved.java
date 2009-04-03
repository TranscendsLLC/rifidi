package org.rifidi.edge.client.model.sal;

import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.core.api.jms.notifications.ReaderRemovedNotification;

/**
 * A command that is executed when a reader was removed
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Command_ReaderRemoved implements RemoteEdgeServerCommand {

	/** The map of remoteReaders */
	private ObservableMap remoteReaders;
	/** ID of reader to remove */
	private String readerID;

	/***
	 * Constructor
	 * 
	 * @param server
	 *            The Remote Edge Server
	 * @param notification
	 *            The JMS notification that was recieved
	 */
	public Command_ReaderRemoved(RemoteEdgeServer server,
			ReaderRemovedNotification notification) {
		this.remoteReaders = server.getRemoteReaders();
		this.readerID = notification.getReaderID();
	}

	@Override
	public void execute() {
		// do nothing
	}

	@Override
	public void executeEclipse() {
		((RemoteReader)remoteReaders.get(readerID)).getRemoteSessions().clear();
		remoteReaders.remove(readerID);

	}

	@Override
	public String getType() {
		return "READER_REMOVED";
	}

}
