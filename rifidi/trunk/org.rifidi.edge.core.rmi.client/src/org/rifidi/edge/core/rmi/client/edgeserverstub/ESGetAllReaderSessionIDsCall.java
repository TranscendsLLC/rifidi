/*
 *  GetAllReaderSesslions.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.core.rmi.client.edgeserverstub;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import org.rifidi.edge.core.rmi.readerconnection.EdgeServerStub;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This Command Object gets all the current session IDs on the Server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ESGetAllReaderSessionIDsCall extends
		ServerDescriptionBasedRemoteMethodCall<Set<Long>, RuntimeException> {

	/**
	 * @param serverDescription
	 *            The description of the RMI stub
	 */
	public ESGetAllReaderSessionIDsCall(ServerDescription serverDescription) {
		super(serverDescription);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.client.remotecall.AbstractRemoteMethodCall#
	 * performRemoteCall(java.rmi.Remote)
	 */
	@Override
	protected Set<Long> performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		EdgeServerStub conreg = (EdgeServerStub) remoteObject;
		return conreg.getAllReaderSessions();
	}

}
