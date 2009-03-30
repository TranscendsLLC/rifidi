package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.api.rmi.ReaderStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This command stops a currently executing session on the reader. It returns
 * null
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RS_StopSession extends
		ServerDescriptionBasedRemoteMethodCall<Object, RuntimeException> {

	/** The ID of the reader */
	private String readerID;
	/** The ID of the session */
	private String sessionID;

	/**
	 * @param serverDescription
	 */
	public RS_StopSession(RS_ServerDescription serverDescription, String readerID,
			String sessionID) {
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
		stub.stopSession(readerID, sessionID);
		return null;
	}
}
