/**
 * 
 */
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import javax.management.AttributeList;

import org.rifidi.edge.core.rmi.ReaderConfigurationStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call gets all properties of all reader configurations that are currently
 * available. This call is intended for when a client first connects to a
 * server.
 * 
 * It returns a Map where the key is the ID of the ReaderConfiguration and the
 * AttributeList are the properties of the ReaderConfiguraiton
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RCGetAllConfigurationProperties
		extends
		ServerDescriptionBasedRemoteMethodCall<Map<String, AttributeList>, RuntimeException> {

	/**
	 * Constructor
	 * @param serverDescription The ServerDescription to use
	 */
	public RCGetAllConfigurationProperties(RCServerDescription serverDescription) {
		super(serverDescription);
	}

	@Override
	protected Map<String, AttributeList> performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderConfigurationStub stub = (ReaderConfigurationStub) remoteObject;
		return stub.getAllReaderConfigurationProperties();
	}

}
