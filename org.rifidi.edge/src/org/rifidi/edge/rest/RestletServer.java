/**
 * 
 */
package org.rifidi.edge.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 * 
 * 
 * @author matt
 */
public class RestletServer {
	
	/** Logger */
	private final Log logger = LogFactory.getLog(getClass());
	
	/**
	 * 
	 */
	public RestletServer(Application app) {
		try {
			
			boolean enabled = Boolean.parseBoolean(System
					.getProperty("org.rifidi.restlet.enabled"));
			if (enabled) {
				int port = Integer.parseInt(System
						.getProperty("org.rifidi.restlet.port"));
				logger.info("Starting restlet server on port: " + port);
				Component component = new Component();
				component.getServers().add(Protocol.HTTP, port);
				component.getClients().add(Protocol.FILE);

				component.getDefaultHost().attach(app);
				component.start();
			}
		} catch (Exception e) {
			// TODO Handle this
			e.printStackTrace();
		}
	}
}
