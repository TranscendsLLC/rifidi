/*
 *  WebServiceRegistar.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.webservice.service;

/**
 * @author kyle
 * 
 */
public interface WebServiceRegistar {

	/**
	 * Register a new web service. The service will be published at
	 * http://ADDRESS:PORT/cxf/serviceName
	 * 
	 * @param serviceName
	 * @param service
	 */
	public void registerWebService(String serviceName, Object service);

	public void unregisterWebServic(String serviceName);

}
