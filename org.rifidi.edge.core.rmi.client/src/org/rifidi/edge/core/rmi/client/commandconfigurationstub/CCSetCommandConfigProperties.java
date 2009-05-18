
package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.management.AttributeList;

import org.rifidi.edge.core.api.rmi.CommandStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This call sets properties on the specified CommandConfiguration. The return
 * AttributeList contains the properties that were set on the
 * CommandConfiguration
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CCSetCommandConfigProperties extends
		ServerDescriptionBasedRemoteMethodCall<AttributeList, RuntimeException> {
	/** The ID of the CommandConfiguration to set the properties of */
	String commandConfigurationID;
	/** The properties to set on the CommandConfiguration */
	AttributeList properties;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription of the CommandStub
	 * @param commandConfigurationID
	 *            the ID of the CommandConfiguration to set the properties of
	 * @param properties
	 *            The properties to set on the CommandConfiguration
	 */
	public CCSetCommandConfigProperties(CCServerDescription serverDescription,
			String commandConfigurationID, AttributeList properties) {
		super(serverDescription);
		this.commandConfigurationID = commandConfigurationID;
		this.properties = properties;
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall(java.rmi.Remote)
	 */
	@Override
	protected AttributeList performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		CommandStub stub = (CommandStub) remoteObject;
		return stub.setCommandProperties(
				this.commandConfigurationID, this.properties);
	}

}
