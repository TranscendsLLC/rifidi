/**
 * 
 */
package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.management.AttributeList;

import org.rifidi.edge.core.rmi.CommandConfigurationStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call gets the properties set on a CommandConfiguration
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CCGetCommandConfigProperties extends
		ServerDescriptionBasedRemoteMethodCall<AttributeList, RuntimeException> {

	/** The ID of the CommandConfiguration to get the properties of */
	private String commandConfigurationID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription of the CommandConfigurationStub
	 * @param commandConfigurationID
	 *            The ID of the CommandConfiguration to get the properties of
	 */
	public CCGetCommandConfigProperties(CCServerDescription serverDescription,
			String commandConfigurationID) {
		super(serverDescription);
		this.commandConfigurationID = commandConfigurationID;
	}

	@Override
	protected AttributeList performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		CommandConfigurationStub stub = (CommandConfigurationStub) remoteObject;
		return stub.getCommandConfigurationProperties(commandConfigurationID);
	}

}
