/*
 * CCGetCommandConfigurations.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.core.rmi.client.commandconfigurationstub;

import java.util.Set;

import org.rifidi.edge.api.rmi.dto.CommandConfigurationDTO;
import org.rifidi.edge.api.rmi.services.CommandManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This call returns the CommandConfiguration objects that are available. It
 * returns a set of CommandConfigurationDTOs for each of the commands
 * configurations that are available
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CCGetCommandConfigurations
		extends
		AbstractRMICommandObject<Set<CommandConfigurationDTO>, java.lang.RuntimeException> {

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The CCServerDecription of the CommandConfiguration stub
	 */
	public CCGetCommandConfigurations(CCServerDescription serverDescription) {
		super(serverDescription);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject#performRemoteCall
	 * (java.lang.Object)
	 */
	@Override
	protected Set<CommandConfigurationDTO> performRemoteCall(Object remoteObject)
			throws RuntimeException {
		CommandManagerService stub = (CommandManagerService) remoteObject;
		return stub.getCommands();
	}

}
