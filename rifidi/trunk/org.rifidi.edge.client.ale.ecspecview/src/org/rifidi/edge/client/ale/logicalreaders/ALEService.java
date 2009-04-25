/**
 * 
 */
package org.rifidi.edge.client.ale.logicalreaders;

import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.Viewer;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ArrayOfString;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.DuplicateNameExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.DuplicateSubscriptionExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ECSpecValidationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.InvalidURIExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchNameExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchSubscriberExceptionResponse;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface ALEService extends IPropertyChangeListener {
	/**
	 * Reload the model.
	 */
	void reload();

	/**
	 * Register a listener for changes to the ALE stub.
	 * 
	 * @param listener
	 */
	void registerALEListener(ALEListener listener);

	/**
	 * Unregister a listener for changes to the ALE stub.
	 * 
	 * @param listener
	 */
	void unregisterALEListener(ALEListener listener);

	/**
	 * Register a viewer to the ALE model.
	 * 
	 * @param viewer
	 */
	void registerALEViewer(Viewer viewer);

	/**
	 * Unregister a viewer to the ALE model.
	 * 
	 * @param viewer
	 */
	void unregisterALEViewer(Viewer viewer);

	/**
	 * Create a new ECSpec on the server.
	 * 
	 * @param name
	 * @param spec
	 * @throws ECSpecValidationExceptionResponse
	 * @throws DuplicateNameExceptionResponse
	 */
	void createECSpec(String name, ECSpec spec)
			throws ECSpecValidationExceptionResponse,
			DuplicateNameExceptionResponse;

	/**
	 * Delete an ECSpec.
	 * 
	 * @param specName
	 * @throws NoSuchNameExceptionResponse
	 */
	void deleteECSpec(String specName) throws NoSuchNameExceptionResponse;

	/**
	 * Get all currently defined ECSpecs.
	 * 
	 */
	List<String> getAvailableECSpecNames();

	/**
	 * Get an ECSpec.
	 * 
	 * @param specName
	 * @throws NoSuchNameExceptionResponse
	 */
	ECSpec getECSpec(String specName) throws NoSuchNameExceptionResponse;

	/**
	 * Subscribe a URI to a spec.
	 * 
	 * @param specName
	 * @param uri
	 * @throws org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchNameExceptionResponse
	 * @throws InvalidURIExceptionResponse
	 * @throws DuplicateSubscriptionExceptionResponse
	 */
	void subscribeECSpec(String specName, String uri)
			throws org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchNameExceptionResponse,
			InvalidURIExceptionResponse, DuplicateSubscriptionExceptionResponse;

	/**
	 * Unsubscribe a URI from a spec.
	 * 
	 * @param specName
	 * @param uri
	 * @throws org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchNameExceptionResponse
	 * @throws InvalidURIExceptionResponse
	 * @throws NoSuchSubscriberExceptionResponse
	 */
	void unsubscribeECSpec(String specName, String uri)
			throws org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchNameExceptionResponse,
			InvalidURIExceptionResponse, NoSuchSubscriberExceptionResponse;

	/**
	 * Get subscribers for a certain ECSpec.
	 * 
	 * @param specName
	 * @throws NoSuchNameExceptionResponse
	 */
	ArrayOfString getSubscribers(String specName)
			throws NoSuchNameExceptionResponse;
}
