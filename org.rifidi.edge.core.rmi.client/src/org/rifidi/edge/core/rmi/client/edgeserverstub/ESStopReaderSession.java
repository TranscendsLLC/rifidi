/**
 * 
 */
package org.rifidi.edge.core.rmi.client.edgeserverstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.rmi.EdgeServerStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ESStopReaderSession extends
		ServerDescriptionBasedRemoteMethodCall<Object, RuntimeException> {

	/** The ID of the ReaderSession to stop */
	private String readerSessionID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescriptionToUse
	 * @param readerSessionID
	 *            The ID of the ReaderSession to stop
	 */
	public ESStopReaderSession(ESServerDescription serverDescription,
			String readerSessionID) {
		super(serverDescription);
		this.readerSessionID = readerSessionID;
	}

	@Override
	protected Object performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		EdgeServerStub stub = (EdgeServerStub) remoteObject;
		stub.stopReaderSession(this.readerSessionID);
		return null;
	}

}
