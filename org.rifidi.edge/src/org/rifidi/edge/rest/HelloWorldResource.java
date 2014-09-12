/**
 * 
 */
package org.rifidi.edge.rest;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * @author matt
 *
 */
public class HelloWorldResource extends ServerResource {
	@Get
    public String represent() {
		String name = (String) getRequest().getAttributes().get("name");
        return "hello, " + name;
    }
}
