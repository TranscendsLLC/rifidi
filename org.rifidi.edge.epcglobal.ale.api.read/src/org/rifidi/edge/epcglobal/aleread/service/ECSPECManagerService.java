/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.service;

import java.util.List;
import java.util.Set;

import org.rifidi.edge.epcglobal.ale.api.read.data.ECSpec;
import org.rifidi.edge.epcglobal.ale.api.read.ws.DuplicateNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.DuplicateSubscriptionExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.InvalidURIExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.NoSuchSubscriberExceptionResponse;
import org.rifidi.edge.epcglobal.aleread.wrappers.RifidiBoundarySpec;
import org.rifidi.edge.epcglobal.aleread.wrappers.RifidiReport;
import org.rifidi.edge.lr.LogicalReader;

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
	 * @param rifidiBoundarySpec
	 * @param readers
	 * @param primarykeys
	 * @param reports
	 * @throws DuplicateNameExceptionResponse
	 * @throws ECSpecValidationExceptionResponse
	 */
	public void createSpec(String name, ECSpec spec,
			RifidiBoundarySpec rifidiBoundarySpec, Set<LogicalReader> readers,
			Set<String> primarykeys, List<RifidiReport> reports) throws DuplicateNameExceptionResponse,
			ECSpecValidationExceptionResponse;

	/**
	 * Destroy an ECSpec.
	 * 
	 * @param name
	 * @throws NoSuchNameExceptionResponse
	 */
	public void destroySpec(String name) throws NoSuchNameExceptionResponse;

	/**
	 * Get a spec by its name.
	 * 
	 * @param name
	 * @return
	 */
	public ECSpec getSpecByName(String name) throws NoSuchNameExceptionResponse;
}
