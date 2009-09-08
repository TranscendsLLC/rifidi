/*
 * CCCreateCommandConfiguration.java
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
 * Create a new CommandConfiguration. The call returns a String that is the ID
 * of the newly created CommandConfiguration
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CCCreateCommandConfiguration extends
		AbstractRMICommandObject<String, RuntimeException> {

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
	 * 
	 * @see
	 * org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject#performRemoteCall
	 * (java.lang.Object)
	 */
	@Override
	protected String performRemoteCall(Object remoteObject)
			throws RuntimeException {
		CommandManagerService stub = (CommandManagerService) remoteObject;
		stub.createCommand(commandConfigurationType, this.properties);
		return "";
	}

}
