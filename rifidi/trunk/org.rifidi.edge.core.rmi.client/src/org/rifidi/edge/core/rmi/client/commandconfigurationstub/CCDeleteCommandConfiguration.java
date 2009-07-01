package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import org.rifidi.edge.api.rmi.services.CommandManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This call deletes a CommandConfiguration with the specified ID. It returns
 * null.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CCDeleteCommandConfiguration extends
		AbstractRMICommandObject<Object, RuntimeException> {

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
	 * 
	 * @see
	 * org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject#performRemoteCall
	 * (java.lang.Object)
	 */
	@Override
	protected Object performRemoteCall(Object remoteObject)
			throws RuntimeException {
		CommandManagerService stub = (CommandManagerService) remoteObject;
		stub.deleteCommand(this.commandConfigurationID);
		return null;

	}

}
