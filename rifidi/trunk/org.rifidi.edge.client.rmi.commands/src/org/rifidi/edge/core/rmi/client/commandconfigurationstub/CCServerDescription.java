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
