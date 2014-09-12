/**
 * 
 */
package org.rifidi.edge.rest;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

/**
 * @author matt
 *
 */
public class RestletServer {
	/**
	 * 
	 */
	public RestletServer(Application app) {
		System.out.println("Starting restlet server");
		Component component = new Component();
        component.getServers().add(Protocol.HTTP, 8111);
        component.getClients().add(Protocol.FILE);
        
        component.getDefaultHost().attach(app);
        try {
			component.start();
		} catch (Exception e) {
			// TODO Handle this
			e.printStackTrace();
		}
	}
}
