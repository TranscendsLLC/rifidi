/* 
 *  ConnectionHandlerWrapper.java
 *  Created:	Mar 20, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.treeview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.treeview.ConnectionHandlerWrapper;

import org.rifidi.edge.client.ale.connection.handler.ConnectionHandler;
import org.rifidi.edge.client.ale.connection.service.ConnectionService;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 *
 */
public class ConnectionHandlerWrapper {

	private ConnectionHandler conHan;

	public ConnectionService getConnectionService(){
		
		ConnectionService service = conHan.getService();
		if(service.getAleEndpoint()==null){
//			if()
		}
		return null;
	}
//	
//	private String getPrefsAleEndpoint
//	
//	private String getPrefsLrEndpont

}
