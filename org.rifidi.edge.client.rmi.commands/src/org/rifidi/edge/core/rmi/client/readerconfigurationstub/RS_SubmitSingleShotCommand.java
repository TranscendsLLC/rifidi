/**
 * 
 */
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import org.rifidi.edge.api.rmi.exceptions.CommandSubmissionException;
import org.rifidi.edge.api.rmi.services.SensorManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * * This command allows you to submit commands to a reader session for a one
 * time execution. It returns null. It throws a CommandSubmissionException if
 * there was a problem * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RS_SubmitSingleShotCommand extends
		AbstractRMICommandObject<Object, CommandSubmissionException> {

	/** The ID of the reader */
	private String readerID;
	/** The id of the session to submit the command to */
	private String sessionID;
	/** The ID of the command to submit */
	private String commandID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The serverDescription to use
	 * @param readerID
	 *            The ID of the reader to submit the command to
	 * @param sessionID
	 *            The ID of the session on the reader
	 * @param commandID
	 *            The ID of the command
	 */
	public RS_SubmitSingleShotCommand(RS_ServerDescription serverDescription,
			String readerID, String sessionID, String commandID) {
		super(serverDescription);
		this.readerID = readerID;
		this.sessionID = sessionID;
		this.commandID = commandID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject#performRemoteCall
	 * (java.lang.Object)
	 */
	@Override
	protected Object performRemoteCall(Object remoteObject)
			throws CommandSubmissionException {
		SensorManagerService stub = (SensorManagerService) remoteObject;
		stub.submitSingleShotCommand(readerID, sessionID, commandID);
		return null;
	}

}
