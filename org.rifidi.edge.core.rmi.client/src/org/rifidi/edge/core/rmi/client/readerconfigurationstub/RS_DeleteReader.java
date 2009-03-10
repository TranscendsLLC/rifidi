/**
 * 
 */
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.api.rmi.ReaderStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call deletes a reader that is currently on the server. The return type
 * is not currently used and will return null
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RS_DeleteReader extends
		ServerDescriptionBasedRemoteMethodCall<Object, RuntimeException> {

	/** The ID of the ReaderConfiguration to delete */
	private String readerID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription to use
	 * @param readerID
	 *            the ID of the Reader to delete
	 */
	public RS_DeleteReader(RS_ServerDescription serverDescription,
			String readerID) {
		super(serverDescription);
		this.readerID = readerID;
	}

	@Override
	protected Object performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderStub stub = (ReaderStub) remoteObject;
		stub.deleteReader(this.readerID);
		return null;
	}

}
