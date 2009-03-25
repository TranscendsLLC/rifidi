/* 
 *  GetEcSpecNames.java
 *  Created:	Mar 20, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.connection
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.connection.commands.impl;

import javax.management.ServiceNotFoundException;

import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ImplementationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.SecurityExceptionResponse;
import org.rifidi.edge.client.ale.connection.commands.WsCommand;
import org.rifidi.edge.client.ale.connection.handler.ConnectionHandler;
import org.rifidi.edge.client.ale.connection.service.ConnectionService;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 *
 */
public class GetEcSpecNames implements WsCommand {

	private Object retO=null;
	/* (non-Javadoc)
	 * @see org.rifidi.edge.client.ale.connection.commands.WsCommand#makeCall()
	 */
	@Override
	public Object makeCall() {
		ConnectionHandler handler = new ConnectionHandler();
		ConnectionService connectionService;
		try {
			connectionService = handler.getService();
		
		if(connectionService==null)throw new RuntimeException("connservice=null");
		ALEServicePortType aleServicePortType = connectionService.getAleServicePortType();
		if(aleServicePortType==null)throw new RuntimeException("Serviceport=null");
		EmptyParms parms = new EmptyParms();
		
			retO=aleServicePortType.getECSpecNames(parms);
		} catch (ImplementationExceptionResponse e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityExceptionResponse e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		handler.ungetService();
		return retO;
		
	}

}
