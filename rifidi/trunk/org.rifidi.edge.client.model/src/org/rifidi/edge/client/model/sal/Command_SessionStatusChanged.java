/**
 * 
 */
package org.rifidi.edge.client.model.sal;
//TODO: Comments
import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.core.api.SessionStatus;
import org.rifidi.edge.core.api.jms.notifications.SessionStatusChangedNotification;

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
