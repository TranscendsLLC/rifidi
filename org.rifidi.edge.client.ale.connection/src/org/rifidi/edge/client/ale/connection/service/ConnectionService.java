/* 
 *  ConnectionService.java
 *  Created:	Mar 19, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.connection
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.connection.service;

import java.net.URL;

import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ALELRServicePortType;
import org.rifidi.edge.client.ale.connection.listeners.EndpointChangeListener;



/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 *
 */
public interface ConnectionService {
	/**
	 * Sets the Endpoint for the Filtering and Collection API.
	 * @param URL endpoint
	 */
	public void setAleEndpoint(URL endpoint);
	/**
	 * Sets the Endpoint for the Logical Reader API.
	 * @param URL endpoint
	 */
	public void setLrEndpoint(URL endpoint);
	/**
	 * Returns the Endpoint for the Filtering and Collection API.
	 * @return URL endpoint
	 */
	public URL getAleEndpoint();
	/**
	 * Returns the Endpoint for the Logical Reader API.
	 * @return URL endpoint
	 */
	public URL getLrEndpoint();
	/**
	 * Subscribes a listener that listens for changes of the Filtering and Collection API endpoint.
	 * @param EndpointChangeListener listener
	 */
	public void subscribeAleChangeListener(EndpointChangeListener listener);
	/**
	 * Subscribes a listener that listens for changes of the Logical Reader API endpoint.
	 * @param EndPointChangeListener listener
	 */
	public void subscribeLRChangeListener(EndpointChangeListener listener);
	/**
	 * Unsubscribes the listener for Filtering and Collection API endpoint changes.
	 * @param EndPointChangeListener listener
	 */
	public void unsubscribeAleChangeListener(EndpointChangeListener listener);
	/**
	 * Unsubscribes the listener for Logical Reader API endpoint changes.
	 * @param EndPointChangeListener listener
	 */
	public void unsubscribeLRChangeListener(EndpointChangeListener listener);
//	public void aleExec(Object executable);
//	public void lrExec(Object executable);
	
	/**
	 * Returns the current ALEServicePortType (Proxy)
	 * @return ALEServicePortType
	 */
	public ALEServicePortType getAleServicePortType();
	/**
	 * Returns the current ALELRServicePortType (Proxy)
	 * @return ALELRServicePortType
	 */
	public ALELRServicePortType getAleLrServicePortType();
	/**
	 * Sets the new Endpoint and then returns the newly generated ALEServicePortType.
	 * @param URL endpoint
	 * @return ALEServicePortType
	 */
	public ALEServicePortType getAleServicePortType(URL endpoint);
	/**
	 * Sets the new Endpoint and then returns the newly generated ALELRServicePortType.
	 * @param URL endpoint
	 * @return ALELRServicePortType
	 */
	public ALELRServicePortType getAleLrServicePortType(URL endpoint);
}
