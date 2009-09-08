/*
 * CCGetCommandConfigFactories.java
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

import org.rifidi.edge.api.rmi.dto.CommandConfigFactoryDTO;
import org.rifidi.edge.api.rmi.services.CommandManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This call gets the available CommandConfiguraiton types. It returns a Set of
 * CommandConfigFactoryDTO that contains the IDs of the command types and the
 * IDs of the ReaderConfigurationFactory that the command type belongs to
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CCGetCommandConfigFactories
		extends
		AbstractRMICommandObject<Set<CommandConfigFactoryDTO>, RuntimeException> {

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription for the CommandConfiguraitonStub
	 */
	public CCGetCommandConfigFactories(CCServerDescription serverDescription) {
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
	protected Set<CommandConfigFactoryDTO> performRemoteCall(Object remoteObject)
			throws RuntimeException {
		CommandManagerService stub = (CommandManagerService) remoteObject;
		return stub.getCommandConfigFactories();
	}

}
