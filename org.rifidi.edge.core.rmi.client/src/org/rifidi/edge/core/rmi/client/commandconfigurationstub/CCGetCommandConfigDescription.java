
package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.management.MBeanInfo;

import org.rifidi.edge.api.rmi.CommandStub;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * Get an MBeanInfo object that contains the meta information that describes
 * CommanConfigurations produced by factories with the supplied type. This
 * information can be used when constructing a new CommandConfiguration
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CCGetCommandConfigDescription extends
		ServerDescriptionBasedRemoteMethodCall<MBeanInfo, RuntimeException> {

	/** The type to get the description fo */
	private String commandConfigTypeID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription of the CommandStub
	 * @param commandConfigTypeID
	 *            The type of ComamndConfiguration to get a description of
	 */
	public CCGetCommandConfigDescription(CCServerDescription serverDescription,
			String commandConfigTypeID) {
		super(serverDescription);
		this.commandConfigTypeID = commandConfigTypeID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall(java.rmi.Remote)
	 */
	@Override
	protected MBeanInfo performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		CommandStub stub = (CommandStub) remoteObject;
		return stub
				.getCommandDescription(this.commandConfigTypeID);
	}

}
