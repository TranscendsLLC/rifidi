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
	public void setAleEndpoint(URL endpoint);
	public void setLrEndpoint(URL endpoint);
	public URL getAleEndpoint();
	public URL getLrEndpoint();
	public void subscribeAleChangeListener(EndpointChangeListener listener);
	public void subscribeLRChangeListener(EndpointChangeListener listener);
	public void unsubscribeAleChangeListener(EndpointChangeListener listener);
	public void unsubscribeLRChangeListener(EndpointChangeListener listener);
//	public void aleExec(Object executable);
//	public void lrExec(Object executable);
	public ALEServicePortType getAleServicePortType();
	public ALELRServicePortType getAleLrServicePortType();
}
