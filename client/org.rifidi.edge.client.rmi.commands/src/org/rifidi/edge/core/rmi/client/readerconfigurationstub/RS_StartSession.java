/*
 * RS_StartSession.java
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

import org.rifidi.edge.api.rmi.services.SensorManagerService;
import org.rifidi.rmi.proxycache.cache.AbstractRMICommandObject;

/**
 * This command starts a session on the reader. It returns null.
 * 
 * @author Kyle Nuemeier - kyle@pramari.com
 */
public class RS_StartSession extends
		AbstractRMICommandObject<Object, RuntimeException> {

	/** The ID of the reader */
	private String readerID;
	/** The index of the session to start */
	private String sessionID;

	/**
	 * @param serverDescription
	 *            The serverDescription
	 * @param readerID
	 *            The ID of the reader to start the session on
	 * @param sessionID
	 *            The index number of the session to start
	 */
	public RS_StartSession(RS_ServerDescription serverDescription,
			String readerID, String sessionID) {
		super(serverDescription);
		this.readerID = readerID;
		this.sessionID = sessionID;
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
		stub.startSession(readerID, sessionID);
		return null;
	}

}
