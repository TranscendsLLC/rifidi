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
 * This command starts a session on the reader. It returns null.
 * 
 * @author Kyle Nuemeier - kyle@pramari.com
 * 
 */
public class RS_StartSession extends
		ServerDescriptionBasedRemoteMethodCall<Object, RuntimeException> {

	/** The ID of the reader */
	private String readerID;
	/** The index of the session to start */
	private String sessionID;

	/**
	 * @param serverDescription
	 *            The serverDescription
	 * @param readerID
	 *            The ID of the reader to start the session on
	 * @param sessionID
	 *            The index number of the session to start
	 */
	public RS_StartSession(RS_ServerDescription serverDescription,
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
		stub.startSession(readerID, sessionID);
		return null;
	}

}
