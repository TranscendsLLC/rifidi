/*
 * RS_GetReaderFactory.java
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

import org.rifidi.edge.api.rmi.dto.ReaderFactoryDTO;
import org.rifidi.edge.api.rmi.services.SensorManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This remote method call gets the DTO of a ReaderFactory. It returns null if
 * one is not available for the supplied ID
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RS_GetReaderFactory extends
		AbstractRMICommandObject<ReaderFactoryDTO, RuntimeException> {

	/** The ID of the factory to get the DTO of */
	private String readerFactoryID;

	/**
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The serverdescription to use
	 * @param readerFactoryID
	 *            The readerFactoryID to get the DTO of
	 */
	public RS_GetReaderFactory(RS_ServerDescription serverDescription,
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
	protected ReaderFactoryDTO performRemoteCall(Object remoteObject)
			throws RuntimeException {
		SensorManagerService stub = (SensorManagerService) remoteObject;
		return stub.getReaderFactory(readerFactoryID);

	}

}
