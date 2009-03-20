/* 
 *  GetVendorVersionAle.java
 *  Created:	Mar 20, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.connection
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.connection.commands.impl;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.connection.commands.WsCommand;
import org.rifidi.edge.client.ale.connection.service.ConnectionService;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class GetVendorVersionAle implements WsCommand {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.client.ale.connection.commands.WsCommand#makeCall()
	 */
	@Override
	public Object makeCall() {
		String sna = "Communication Service not available";
		String spe = "ServicePortType is null - probably no Endpoint set";

		BundleContext context = InternalPlatform.getDefault()
				.getBundleContext();

		ServiceReference serviceReference = context
				.getServiceReference(ConnectionService.class.getName());
		if (serviceReference == null)
			return sna;

		ConnectionService connectionService = (ConnectionService) context
				.getService(serviceReference);
		if (connectionService == null)
			return sna;
		if (connectionService.getAleServicePortType() == null)
			return spe;

		EmptyParms parms = new EmptyParms();
		try {
			return connectionService.getAleServicePortType().getVendorVersion(
					parms);
		} catch (Exception e) {
			e.printStackTrace();
			return new Object();
		}

	}

}
