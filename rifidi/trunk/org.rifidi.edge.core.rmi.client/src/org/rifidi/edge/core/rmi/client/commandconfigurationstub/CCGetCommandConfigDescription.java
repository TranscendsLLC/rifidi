/**
 * 
 */
package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.rmi.CommandConfigurationStub;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * Get an MBeanInfo object that contains the meta information that describes
 * CommanConfigurations produced by factories with the supplied type. This
 * information can be used when constructing a new CommandConfiguration
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CCGetCommandConfigDescription extends
		ServerDescriptionBasedRemoteMethodCall<MBeanInfo, RuntimeException> {

	/** The type to get the description fo */
	private String commandConfigTypeID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription of the CommandConfigurationStub
	 * @param commandConfigTypeID
	 *            The type of ComamndConfiguration to get a description of
	 */
	public CCGetCommandConfigDescription(ServerDescription serverDescription,
			String commandConfigTypeID) {
		super(serverDescription);
		this.commandConfigTypeID = commandConfigTypeID;
	}

	@Override
	protected MBeanInfo performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		CommandConfigurationStub stub = (CommandConfigurationStub) remoteObject;
		return stub
				.getCommandConfigurationDescription(this.commandConfigTypeID);
	}

}
