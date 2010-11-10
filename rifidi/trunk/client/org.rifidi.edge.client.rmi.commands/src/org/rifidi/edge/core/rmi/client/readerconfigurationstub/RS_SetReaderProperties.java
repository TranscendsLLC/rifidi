/*
 * RS_SetReaderProperties.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.core.rmi.client.readerconfigurationstub;

import javax.management.AttributeList;

import org.rifidi.edge.api.rmi.services.SensorManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This method sets properties on a specified Reader. The return value is not
 * currenlty used, and it returns null
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RS_SetReaderProperties extends
		AbstractRMICommandObject<Object, RuntimeException> {

	/** The new properties to set on the reader */
	private AttributeList attributes;
	/** The ID of the reader configuration */
	private String readerID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The SeverdDescription to use
	 * @param readerID
	 *            The ID of the Reader
	 * @param readerProperties
	 *            The properties to set on the ReaderConfiguration
	 */
	public RS_SetReaderProperties(RS_ServerDescription serverDescription,
			String readerID, AttributeList readerProperties) {
		super(serverDescription);
		this.attributes = readerProperties;
		this.readerID = readerID;
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
		SensorManagerService stub = (SensorManagerService) remoteObject;
		stub.setReaderProperties(readerID, attributes);
		return null;

	}

}
