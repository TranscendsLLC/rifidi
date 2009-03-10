/**
 * 
 */
package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import org.rifidi.edge.core.api.rmi.CommandStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call gets the available CommandConfiguraiton types. It returns a
 * Map<String,String> where the keys are the IDs of the command types and the
 * values are the IDs of the ReaderConfigurationFactory that the command type
 * belongs to
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CCGetCommandConfigTypes
		extends
		ServerDescriptionBasedRemoteMethodCall<Map<String, String>, RuntimeException> {

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription for the CommandConfiguraitonStub
	 */
	public CCGetCommandConfigTypes(CCServerDescription serverDescription) {
		super(serverDescription);
	}

	@Override
	protected Map<String, String> performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		CommandStub stub = (CommandStub) remoteObject;
		return stub.getCommandConfigurationTypes();
	}

}
