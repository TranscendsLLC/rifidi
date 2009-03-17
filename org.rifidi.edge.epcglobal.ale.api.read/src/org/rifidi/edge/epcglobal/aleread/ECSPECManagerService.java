/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread;

import java.util.Set;

import org.rifidi.edge.epcglobal.ale.api.read.data.ECSpec;
import org.rifidi.edge.epcglobal.ale.api.read.ws.DuplicateNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.DuplicateSubscriptionExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.InvalidURIExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.NoSuchSubscriberExceptionResponse;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface ECSPECManagerService {
	/**
	 * Get the names of currently available specs.
	 */
	public Set<String> getNames();

	/**
	 * Subscribe to a spec.
	 * 
	 * @param specName
	 * @param uri
	 *            target for the reports
	 * @throws NoSuchNameExceptionResponse
	 * @throws DuplicateSubscriptionExceptionResponse
	 * @throws InvalidURIExceptionResponse
	 */
	public void subscribe(String specName, String uri)
			throws NoSuchNameExceptionResponse,
			DuplicateSubscriptionExceptionResponse, InvalidURIExceptionResponse;

	/**
	 * Unsubscribe from a spec.
	 * 
	 * @param specName
	 * @param uri
	 *            target for the reports
	 * @throws NoSuchNameExceptionResponse
	 * @throws DuplicateSubscriptionExceptionResponse
	 * @throws InvalidURIExceptionResponse
	 */
	public void unsubscribe(String specName, String uri)
			throws NoSuchNameExceptionResponse,
			NoSuchSubscriberExceptionResponse, InvalidURIExceptionResponse;

	/**
	 * Create a new spec with the given name.
	 * 
	 * @param name
	 * @param spec
	 * @throws DuplicateNameExceptionResponse
	 * @throws ECSpecValidationExceptionResponse
	 */
	public void createSpec(String name, ECSpec spec)
			throws DuplicateNameExceptionResponse,
			ECSpecValidationExceptionResponse;

	/**
	 * Get a spec by its name.
	 * 
	 * @param name
	 * @return
	 */
	public ECSpec getSpecByName(String name) throws NoSuchNameExceptionResponse;
}
