package org.rifidi.edge.rest;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class RestletStopSession extends ServerResource {
	
	public RestletStopSession() {
		super();
		System.out.println("Called the restlet stop session constructor!  ");
	}
	
	
	
	@Get
	public String toString() {
		return "stopsession called";
	}

}
