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
 * A command to kill commands that are repeated. It returns null
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RS_KillCommand extends
		ServerDescriptionBasedRemoteMethodCall<Object, RuntimeException> {

	/** The reader ID */
	private String readerID;
	/** The session index */
	private String sessionID;
	/** The process ID of the command to kill */
	private Integer processID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The server to use
	 * @param readerID
	 *            The ID of the reader
	 * @param sessionID
	 *            The ID of the session
	 * @param processID
	 *            The process ID of the command to kill
	 */
	public RS_KillCommand(RS_ServerDescription serverDescription, String readerID,
			String sessionID, Integer processID) {
		super(serverDescription);
		this.readerID = readerID;
		this.sessionID= sessionID;
		this.processID = processID;
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
		stub.killCommand(readerID, sessionID, processID);
		return null;
	}

}
