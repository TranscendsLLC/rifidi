package org.rifidi.edge.client.model.sal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.rifidi.edge.api.jms.notifications.SessionAddedNotification;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.client.model.sal.commands.RequestExecuterSingleton;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_GetSession;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_ServerDescription;
import org.rifidi.rmi.proxycache.exceptions.AuthenticationException;
import org.rifidi.rmi.proxycache.exceptions.ServerUnavailable;

/**
 * A command that is executed when a new Session was added
 * 
 * @author Kyle Neumeier - Kyle@pramari.com
 */
public class Command_SessionAdded implements RemoteEdgeServerCommand {

	/** The Server Description of the Reader Stub */
	private RS_ServerDescription description;
	/** The ID of the reader that the session was added to */
	private String readerID;
	/** The ID of the session that was added */
	private String sessionID;
	/** A command to execute if there was a problem */
	private Command_Disconnect disconnectCommand;
	/** The DTO of the session */
	private SessionDTO sessionDTO;
	/** The list of readers */
	private ObservableMap readers;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(Command_SessionAdded.class);
	/** A variable that is set if there was an error */
	private boolean error = false;

	/***
	 * Constructor
	 * 
	 * @param server
	 *            The RemoteEdgeServer instance
	 * @param notification
	 *            The Notification that was received
	 */
	public Command_SessionAdded(RemoteEdgeServer server,
			SessionAddedNotification notification) {
		description = server.getRSServerDescription();
		readerID = notification.getReaderID();
		sessionID = notification.getSessionID();
		disconnectCommand = new Command_Disconnect(server);
		readers = server.getRemoteReaders();

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
		// make a RMI call to get the Session DTO
		RS_GetSession call = new RS_GetSession(description, readerID, sessionID);
		try {
			sessionDTO = call.makeCall();
		} catch (ServerUnavailable e) {
			error = true;
			RequestExecuterSingleton.getInstance().scheduleRequest(
					disconnectCommand);
		} catch (AuthenticationException e) {
			logger.warn("Authentication Exception ", e);
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
		ObservableMap sessions = null;
		// get the session out of the reader
		RemoteReader reader = (RemoteReader) readers.get(readerID);
		if (reader != null) {
			sessions = reader.getRemoteSessions();
		}
		// If there was no error, put create a new RemoteSession and put it in
		// the map
		if (sessionDTO != null && sessions != null && !error) {
			sessions.put(sessionDTO.getID(), new RemoteSession(readerID, reader
					.getFactoryID(), sessionDTO));
		} else {
			logger.warn("Cannot add session to observable map");
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
		return "SESSION_ADDED";
	}

}
