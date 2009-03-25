/* 
 *  ConnectionHandler.java
 *  Created:	Mar 20, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.connection
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.connection.handler;

import javax.management.ServiceNotFoundException;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.rifidi.edge.client.ale.connection.service.ConnectionService;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ConnectionHandler {
	private BundleContext context = null;
	private ServiceReference serviceReference = null;

	public ConnectionService getService() throws ServiceNotFoundException {

		context = InternalPlatform.getDefault().getBundleContext();

		serviceReference = context.getServiceReference(ConnectionService.class
				.getName());
		if (serviceReference == null) {

			throw new ServiceNotFoundException("serviceref=null");
		}

		ConnectionService connectionService = (ConnectionService) context
				.getService(serviceReference);
		if (connectionService == null) {
			throw new ServiceNotFoundException("service=null");
		}
		return connectionService;
	}

	public void ungetService() {
		if (context != null || serviceReference != null)
			context.ungetService(serviceReference);
	}

}
