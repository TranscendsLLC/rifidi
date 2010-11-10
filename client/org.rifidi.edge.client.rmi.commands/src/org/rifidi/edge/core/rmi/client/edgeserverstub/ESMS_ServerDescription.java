/*
 * ESMS_ServerDescription.java
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
import org.rifidi.rmi.proxycache.cache.ServerDescription;
import org.springframework.security.Authentication;

/**
 * A server description for the EdgeServerManagerService. It is used to look up
 * the Remote stub in the RMI registry.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ESMS_ServerDescription extends ServerDescription {

	/**
	 * Constructor
	 * 
	 * @param serverIP
	 *            The IP address of the server
	 * @param serverPort
	 *            The port
	 * @param authentication
	 *            An authentication object used to verify calls made to this
	 *            server. This object is not used to determine ServerDescription
	 *            uniqueness
	 */
	public ESMS_ServerDescription(String serverIP, int serverPort,
			Authentication authentication) {
		super(serverIP, serverPort, "EdgeServerManagerService",
				EdgeServerManagerService.class, authentication);
	}

}
