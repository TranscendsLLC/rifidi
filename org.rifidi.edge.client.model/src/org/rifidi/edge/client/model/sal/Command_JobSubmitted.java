/**
 * 
 */
package org.rifidi.edge.client.model.sal;

import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.core.api.jms.notifications.JobSubmittedNotification;

/**
 * @author kyle
 * 
 */
public class Command_JobSubmitted implements RemoteEdgeServerCommand {

	private RemoteEdgeServer server;
	private JobSubmittedNotification notification;

	public Command_JobSubmitted(RemoteEdgeServer server,
			JobSubmittedNotification notification) {
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
		//do nothing

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
				session.getRemoteJobs().put(
						this.notification.getJobID(),
						new RemoteJob(notification.getReaderID(), notification
								.getSessionID(), notification.getJobID(),
								notification.getCommandConfigurationID()));
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
		return "JOB_SUBMITTED";
	}

}
