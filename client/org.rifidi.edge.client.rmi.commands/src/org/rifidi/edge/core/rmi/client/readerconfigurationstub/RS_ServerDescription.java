/*
 * RS_ServerDescription.java
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
import org.rifidi.rmi.proxycache.cache.ServerDescription;
import org.springframework.security.Authentication;

/**
 * This is a server description for a ReaderStub
 * 
 * @author Kyle Neumeier - kyl@pramari.com
 */
public class RS_ServerDescription extends ServerDescription {

	/**
	 * Constructor
	 * 
	 * @param serverIP
	 *            The IP address of the server
	 * @param serverPort
	 *            The RMI port that the ReaderStub is exposed on
	 */
	public RS_ServerDescription(String serverIP, int serverPort,
			Authentication authentication) {
		super(serverIP, serverPort, "SensorManagerService",
				SensorManagerService.class, authentication);
	}

}
