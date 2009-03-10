/**
 * 
 */
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.rifidi.edge.core.api.rmi.ReaderStub;
import org.rifidi.edge.core.api.rmi.dto.SessionDTO;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This command creates a new session on the reader. It retuns a List of
 * ReaderSession data transfer objects that give information about all the
 * sessions currently available on that reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RS_CreateSession
		extends
		ServerDescriptionBasedRemoteMethodCall<List<SessionDTO>, RuntimeException> {

	/** The readerID to create a session on */
	private String readerID;

	/**
	 * @param serverDescription
	 */
	public RS_CreateSession(ServerDescription serverDescription, String readerID) {
		super(serverDescription);
		this.readerID = readerID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall
	 * (java.rmi.Remote)
	 */
	@Override
	protected List<SessionDTO> performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderStub stub = (ReaderStub) remoteObject;
		return stub.createSession(readerID);
	}

}
