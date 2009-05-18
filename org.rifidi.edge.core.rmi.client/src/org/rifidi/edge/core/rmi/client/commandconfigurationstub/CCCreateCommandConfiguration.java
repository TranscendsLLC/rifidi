
package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.management.AttributeList;

import org.rifidi.edge.core.api.rmi.CommandStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * Create a new CommandConfiguration. The call returns a String that is the ID
 * of the newly created CommandConfiguration
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CCCreateCommandConfiguration extends
		ServerDescriptionBasedRemoteMethodCall<String, RuntimeException> {

	/** The type of CommandConfiguration to create */
	private String commandConfigurationType;
	/** The properties to set on the new CommandConfiguration */
	private AttributeList properties;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            the ServerDescription of the CommandStub
	 * @param comamndConfigurationType
	 *            The type of CommandConfiguration to create
	 * @param properties
	 *            The properties to set on the new CommandConfiguration
	 */
	public CCCreateCommandConfiguration(CCServerDescription serverDescription,
			String comamndConfigurationType, AttributeList properties) {
		super(serverDescription);
		this.properties = properties;
		this.commandConfigurationType = comamndConfigurationType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall(java.rmi.Remote)
	 */
	@Override
	protected String performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		CommandStub stub = (CommandStub) remoteObject;
		return stub.createCommand(commandConfigurationType,
				this.properties);
	}

}
