
package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.api.rmi.CommandStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call deletes a CommandConfiguration with the specified ID. It returns
 * null.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CCDeleteCommandConfiguration extends
		ServerDescriptionBasedRemoteMethodCall<Object, RuntimeException> {

	/** The ID of the CommandConfiguration to delete */
	private String commandConfigurationID;

	/***
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription of the CommandStub
	 * @param commandConfigurationID
	 *            The ID of the CommandConfiguration to delete
	 */
	public CCDeleteCommandConfiguration(CCServerDescription serverDescription,
			String commandConfigurationID) {
		super(serverDescription);
		this.commandConfigurationID = commandConfigurationID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall(java.rmi.Remote)
	 */
	@Override
	protected Object performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		CommandStub stub = (CommandStub) remoteObject;
		stub.deleteCommand(this.commandConfigurationID);
		return null;
	}

}
