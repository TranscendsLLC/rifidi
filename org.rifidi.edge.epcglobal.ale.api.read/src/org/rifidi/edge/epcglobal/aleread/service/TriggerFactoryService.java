package org.rifidi.edge.epcglobal.aleread.service;

import org.rifidi.edge.epcglobal.ale.api.read.ws.InvalidURIExceptionResponse;
import org.rifidi.edge.epcglobal.aleread.Trigger;

/**
 * This factory creates triggers based on their URI.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface TriggerFactoryService {

	/**
	 * Create a trigger for a URI.
	 * 
	 * @param uri
	 * @return
	 * @throws InvalidURIExceptionResponse
	 */
	public abstract Trigger createTrigger(String uri)
			throws InvalidURIExceptionResponse;

}