/**
 * 
 */
package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import org.rifidi.edge.core.api.rmi.CommandStub;
import org.rifidi.edge.core.api.rmi.dto.CommandConfigurationDTO;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call returns the CommandConfiguration objects that are available. It
 * returns a set of CommandConfigurationDTOs for each of the commands
 * configurations that are available
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CCGetCommandConfigurations
		extends
		ServerDescriptionBasedRemoteMethodCall<Set<CommandConfigurationDTO>, java.lang.RuntimeException> {

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
	protected Set<CommandConfigurationDTO> performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		CommandStub stub = (CommandStub) remoteObject;
		return stub.getCommands();
	}

}
