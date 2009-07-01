package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import javax.management.MBeanInfo;

import org.rifidi.edge.api.rmi.services.CommandManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * Get an MBeanInfo object that contains the meta information that describes
 * CommanConfigurations produced by factories with the supplied type. This
 * information can be used when constructing a new CommandConfiguration
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CCGetCommandConfigDescription extends
		AbstractRMICommandObject<MBeanInfo, RuntimeException> {

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
	 * 
	 * @see
	 * org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject#performRemoteCall
	 * (java.lang.Object)
	 */
	@Override
	protected MBeanInfo performRemoteCall(Object remoteObject)
			throws RuntimeException {
		CommandManagerService stub = (CommandManagerService) remoteObject;
		return stub.getCommandDescription(this.commandConfigTypeID);

	}

}
