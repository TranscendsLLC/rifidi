/*
 * RS_GetReaderDescription.java
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

import javax.management.MBeanInfo;

import org.rifidi.edge.api.rmi.services.SensorManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This call gets an MBeanInfo object that describes the Readers that are
 * produced from this factory. This information can be used to construct a new
 * reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RS_GetReaderDescription extends
		AbstractRMICommandObject<MBeanInfo, RuntimeException> {

	/** The supplied readerConfiguraitonFactoryID */
	private String readerFactoryID;

	/**
	 * 
	 * @param serverDescription
	 *            The serverdescription
	 * @param readerFactoryID
	 *            the ID of the reader Factory to get a description of
	 */
	public RS_GetReaderDescription(RS_ServerDescription serverDescription,
			String readerFactoryID) {
		super(serverDescription);
		this.readerFactoryID = readerFactoryID;
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
		SensorManagerService stub = (SensorManagerService) remoteObject;
		return stub.getReaderDescription(readerFactoryID);
	}

}
