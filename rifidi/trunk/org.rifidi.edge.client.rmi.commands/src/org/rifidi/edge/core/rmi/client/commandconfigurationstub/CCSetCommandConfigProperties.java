/*
 * CCSetCommandConfigProperties.java
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

import javax.management.AttributeList;

import org.rifidi.edge.api.rmi.services.CommandManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This call sets properties on the specified CommandConfiguration. The return
 * AttributeList contains the properties that were set on the
 * CommandConfiguration
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CCSetCommandConfigProperties extends
		AbstractRMICommandObject<AttributeList, RuntimeException> {
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
	 * 
	 * @see
	 * org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject#performRemoteCall
	 * (java.lang.Object)
	 */
	@Override
	protected AttributeList performRemoteCall(Object remoteObject)
			throws RuntimeException {
		CommandManagerService stub = (CommandManagerService) remoteObject;
		stub.setCommandProperties(this.commandConfigurationID, this.properties);
		return null;
	}

}
