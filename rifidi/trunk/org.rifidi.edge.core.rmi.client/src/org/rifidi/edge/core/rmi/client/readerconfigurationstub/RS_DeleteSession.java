/**
 * 
 */
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;
//TODO: Comments
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.api.rmi.ReaderStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This command deletes a Reader Session.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RS_DeleteSession extends
		ServerDescriptionBasedRemoteMethodCall<Object, RuntimeException> {

	/** The ID of the reader */
	private String readerID;
	/** The ID of the session to delete */
	private String sessionID;

	/**
	 * 
	 * @param serverDescription
	 *            The server description to use
	 * @param readerID
	 *            The ID of the reader which has the session
	 * @param sessionID
	 *            The ID of the session to delete
	 */
	public RS_DeleteSession(RS_ServerDescription serverDescription,
			String readerID, String sessionID) {
		super(serverDescription);
		this.readerID = readerID;
		this.sessionID = sessionID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall
	 * (java.rmi.Remote)
	 */
	@Override
	protected Object performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderStub stub = (ReaderStub) remoteObject;
		stub.deleteSession(readerID, sessionID);
		return null;
	}

}
