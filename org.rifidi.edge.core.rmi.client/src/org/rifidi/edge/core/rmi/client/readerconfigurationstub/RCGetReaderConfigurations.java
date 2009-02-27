/**
 * 
 */
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import org.rifidi.edge.core.rmi.ReaderConfigurationStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This Remote call returns the reader configurations that are available on the
 * server. It returns Map<String, String> where the key is the reader
 * configuration ID and the value is the ID of the reader configuration factory
 * which created the configuration
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RCGetReaderConfigurations
		extends
		ServerDescriptionBasedRemoteMethodCall<Map<String, String>, RuntimeException> {

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription to use
	 */
	public RCGetReaderConfigurations(RCServerDescription serverDescription) {
		super(serverDescription);
	}

	@Override
	protected Map<String, String> performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderConfigurationStub stub = (ReaderConfigurationStub) remoteObject;
		return stub.getAvailableReaderConfigurations();
	}

}
