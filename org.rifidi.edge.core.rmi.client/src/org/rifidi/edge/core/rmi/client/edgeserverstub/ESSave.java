/**
 * 
 */
package org.rifidi.edge.core.rmi.client.edgeserverstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.rmi.EdgeServerStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call saves the current properties of all configurations (e.g.
 * ReaderConfiguration, CommandConfiguration) to the configuration file
 * 
 * makeCall() currently returns a null object
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ESSave extends
		ServerDescriptionBasedRemoteMethodCall<Object, RuntimeException> {

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription to use
	 */
	public ESSave(ESServerDescription serverDescription) {
		super(serverDescription);
	}

	@Override
	protected Object performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		EdgeServerStub stub = (EdgeServerStub) remoteObject;
		stub.save();
		return null;
	}

}
