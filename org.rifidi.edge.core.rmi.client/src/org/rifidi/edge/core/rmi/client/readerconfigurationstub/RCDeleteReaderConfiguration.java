/**
 * 
 */
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.api.ReaderStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call deletes a reader configuration that is currently on the server. The
 * return type is not currently used and will return null
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RCDeleteReaderConfiguration extends
		ServerDescriptionBasedRemoteMethodCall<Object, RuntimeException> {

	/** The ID of the ReaderConfiguration to delete */
	private String readerConfigurationID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription to use
	 * @param readerConfigurationID
	 *            the ID of the ReaderConfiguration to delete
	 */
	public RCDeleteReaderConfiguration(RCServerDescription serverDescription,
			String readerConfigurationID) {
		super(serverDescription);
		this.readerConfigurationID = readerConfigurationID;
	}

	@Override
	protected Object performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderStub stub = (ReaderStub) remoteObject;
		stub.deleteReader(this.readerConfigurationID);
		return null;
	}

}
