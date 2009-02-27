/**
 * 
 */
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import org.rifidi.edge.core.rmi.ReaderConfigurationStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This remote call returns a list of ReaderConfigurationFactory IDs. This list
 * corresponds to the "reader plugins" that are available.
 * 
 * @author Kyle Neumeier - Kyle Neumeier
 * 
 */
public class RCGetReaderConfigFactories extends
		ServerDescriptionBasedRemoteMethodCall<Set<String>, RuntimeException> {

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            the server description
	 */
	public RCGetReaderConfigFactories(RCServerDescription serverDescription) {
		super(serverDescription);
	}

	@Override
	protected Set<String> performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderConfigurationStub stub = (ReaderConfigurationStub) remoteObject;
		return stub.getAvailableReaderConfigurationFactories();
	}

}
