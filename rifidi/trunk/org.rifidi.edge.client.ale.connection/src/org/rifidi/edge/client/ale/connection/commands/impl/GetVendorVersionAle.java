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

import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.connection.commands.WsCommand;
import org.rifidi.edge.client.ale.connection.handler.ConnectionHandler;
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
		ConnectionHandler handler = new ConnectionHandler();

		try {
			ConnectionService connectionService = handler.getService();
			if (connectionService.getAleServicePortType() == null)
				throw new RuntimeException("ServicePortType is null");

			EmptyParms parms = new EmptyParms();
			return connectionService.getAleServicePortType().getVendorVersion(
					parms);
		} catch (Exception e) {
			return e.getMessage();
		} finally {
			handler.ungetService();
		}

	}

}
