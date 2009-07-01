package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import org.rifidi.edge.api.rmi.dto.CommandConfigurationDTO;
import org.rifidi.edge.api.rmi.services.CommandManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This RMI call gets A CommandConfigurationDTO for a certain
 * CommandConfigurtion. It returns null if no configuration for the given ID is
 * available
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CCGetCommandConfiguration extends
		AbstractRMICommandObject<CommandConfigurationDTO, RuntimeException> {

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
	 * org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject#performRemoteCall
	 * (java.lang.Object)
	 */
	@Override
	protected CommandConfigurationDTO performRemoteCall(Object remoteObject)
			throws RuntimeException {
		CommandManagerService stub = (CommandManagerService) remoteObject;
		return stub.getCommandConfiguration(commandConfigurationID);
	}

}
