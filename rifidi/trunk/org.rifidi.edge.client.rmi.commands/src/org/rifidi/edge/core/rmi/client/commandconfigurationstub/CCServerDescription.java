/*
 * CCServerDescription.java
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

import org.rifidi.edge.api.rmi.services.CommandManagerService;
import org.rifidi.rmi.proxycache.cache.ServerDescription;
import org.springframework.security.Authentication;

/**
 * The ServerDescription for the CommandStub
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CCServerDescription extends ServerDescription {

	/**
	 * Constructor
	 * 
	 * @param serverIP
	 *            The IP address of the server
	 * @param serverPort
	 *            The RMI port of the server
	 */
	public CCServerDescription(String serverIP, int serverPort,
			Authentication authentication) {
		super(serverIP, serverPort, "CommandManagerService",
				CommandManagerService.class, authentication);
	}
}
