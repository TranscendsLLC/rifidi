/*
 * ESMS_SaveCommand.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.core.rmi.client.edgeserverstub;

import org.rifidi.edge.api.rmi.services.EdgeServerManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This call saves the current properties of all configurations (e.g.
 * ReaderConfiguration, CommandConfiguration) to the configuration file
 * 
 * makeCall() currently returns a null object
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ESMS_SaveCommand extends AbstractRMICommandObject<Object, RuntimeException> {

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription to use
	 */
	public ESMS_SaveCommand(ESMS_ServerDescription serverDescription) {
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
	protected Object performRemoteCall(Object remoteObject)
			throws RuntimeException {
		((EdgeServerManagerService) remoteObject).save();
		return null;
	}

}
