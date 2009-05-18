package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.api.rmi.CommandStub;
import org.rifidi.edge.core.api.rmi.dto.CommandConfigurationDTO;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This RMI call gets A CommandConfigurationDTO for a certain
 * CommandConfigurtion. It returns null if no configuration for the given ID is
 * available
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CCGetCommandConfiguration
		extends
		ServerDescriptionBasedRemoteMethodCall<CommandConfigurationDTO, RuntimeException> {

	private String commandConfigurationID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 * @param commandConfigurationID
	 */
	public CCGetCommandConfiguration(CCServerDescription serverDescription,
			String commandConfigurationID) {
		super(serverDescription);
		this.commandConfigurationID = commandConfigurationID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall
	 * (java.rmi.Remote)
	 */
	@Override
	protected CommandConfigurationDTO performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		CommandStub stub = (CommandStub) remoteObject;
		return stub.getCommandConfiguration(commandConfigurationID);
	}

}
