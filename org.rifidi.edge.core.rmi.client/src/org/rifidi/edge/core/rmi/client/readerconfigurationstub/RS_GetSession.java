
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.api.rmi.ReaderStub;
import org.rifidi.edge.core.api.rmi.dto.SessionDTO;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * TODO: Class level comment.  
 * 
 * @author kyle
 */
public class RS_GetSession extends
		ServerDescriptionBasedRemoteMethodCall<SessionDTO, RuntimeException> {

	private String readerID;
	private String sessionID;

	/**
	 * Constructor.  
	 * 
	 * @param serverDescription
	 */
	public RS_GetSession(RS_ServerDescription serverDescription, String readerID,
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
	protected SessionDTO performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderStub stub = (ReaderStub) remoteObject;
		return stub.getSession(readerID, sessionID);
	}

}
