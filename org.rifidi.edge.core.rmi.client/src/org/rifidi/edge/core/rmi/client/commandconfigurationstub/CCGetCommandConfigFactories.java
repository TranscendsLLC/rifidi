/**
 * 
 */
package org.rifidi.edge.core.rmi.client.commandconfigurationstub;
//TODO: Comments
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import org.rifidi.edge.core.api.rmi.CommandStub;
import org.rifidi.edge.core.api.rmi.dto.CommandConfigFactoryDTO;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call gets the available CommandConfiguraiton types. It returns a Set of
 * CommandConfigFactoryDTO that contains the IDs of the command types and the IDs
 * of the ReaderConfigurationFactory that the command type belongs to
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CCGetCommandConfigFactories
		extends
		ServerDescriptionBasedRemoteMethodCall<Set<CommandConfigFactoryDTO>, RuntimeException> {

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription for the CommandConfiguraitonStub
	 */
	public CCGetCommandConfigFactories(CCServerDescription serverDescription) {
		super(serverDescription);
	}

	@Override
	protected Set<CommandConfigFactoryDTO> performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		CommandStub stub = (CommandStub) remoteObject;
		return stub.getCommandConfigFactories();
	}

}
