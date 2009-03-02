/**
 * 
 */
package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import org.rifidi.edge.core.rmi.CommandConfigurationStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call returns the CommandConfiguration objects that are available. It
 * returns a Map<String,String> where the keys are the IDs of the
 * CommandConfigurations and the values are the IDs of the CommandConfiguration
 * types. For example: <Alien9800-GetTagList-1, Alien9800-GetTagList>
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CCGetCommandConfigurations
		extends
		ServerDescriptionBasedRemoteMethodCall<Map<String, String>, java.lang.RuntimeException> {

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The CCServerDecription of the CommandConfiguration stub
	 */
	public CCGetCommandConfigurations(CCServerDescription serverDescription) {
		super(serverDescription);
	}

	@Override
	protected Map<String, String> performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		CommandConfigurationStub stub = (CommandConfigurationStub) remoteObject;
		return stub.getCommandConfigurations();
	}

}
