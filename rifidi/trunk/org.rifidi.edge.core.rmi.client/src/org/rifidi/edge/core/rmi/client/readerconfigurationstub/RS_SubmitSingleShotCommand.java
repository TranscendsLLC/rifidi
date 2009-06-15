/**
 * 
 */
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.api.rmi.ReaderStub;
import org.rifidi.edge.core.api.rmi.exceptions.CommandSubmissionException;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * * This command allows you to submit commands to a reader session for a one
 * time execution. It returns null. It throws a CommandSubmissionException if
 * there was a problem * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RS_SubmitSingleShotCommand
		extends
		ServerDescriptionBasedRemoteMethodCall<Object, CommandSubmissionException> {

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

	@Override
	protected Object performRemoteCall(Remote remoteObject)
			throws RemoteException, CommandSubmissionException {
		ReaderStub stub = (ReaderStub) remoteObject;
		stub.submitSingleShotCommand(readerID, sessionID, commandID);
		return null;
	}

}
