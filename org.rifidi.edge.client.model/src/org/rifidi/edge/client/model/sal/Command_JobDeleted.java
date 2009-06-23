package org.rifidi.edge.client.model.sal;

import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.api.jms.notifications.JobDeletedNotification;

/**
 * A handler for a notification when a Job is deleted from a session
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class Command_JobDeleted implements RemoteEdgeServerCommand {

	private RemoteEdgeServer server;
	private JobDeletedNotification notification;

	/**
	 * Constructor
	 * 
	 * @param server
	 *            instance of a RemoteEdgeServer
	 * @param notification
	 *            the notification message
	 */
	public Command_JobDeleted(RemoteEdgeServer server,
			JobDeletedNotification notification) {
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
				this.notification.getReaderID());
		if (reader != null) {
			RemoteSession session = (RemoteSession) reader.getRemoteSessions()
					.get(this.notification.getSessionID());
			if (session != null) {
				session.getRemoteJobs().remove(notification.getJobID());
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
		return "JOB_DELETED";
	}

}
