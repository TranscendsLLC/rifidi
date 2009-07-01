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
