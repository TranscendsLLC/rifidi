/*
 *  DeleteReaderSessionCommand.java
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

import org.rifidi.edge.core.rmi.api.readerconnection.EdgeServerStub;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionServerDescription;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.cache.util.RemoteStubCacheUtil;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * A command object that deletes a reader session
 * 
 * @author kyle
 * 
 */
public class ESDeleteReaderSessionCall extends
		ServerDescriptionBasedRemoteMethodCall<Object, RuntimeException> {

	private Long sessionID;

	/**
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 * @param sessionID
	 *            The sessionID of the session to delete
	 */
	public ESDeleteReaderSessionCall(ServerDescription serverDescription,
			Long sessionID) {
		super(serverDescription);
		this.sessionID = sessionID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.client.remotecall.AbstractRemoteMethodCall#
	 * performRemoteCall(java.rmi.Remote)
	 */
	@Override
	protected Object performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {

		EdgeServerStub conreg = (EdgeServerStub) remoteObject;
		conreg.deleteReaderSession(sessionID);

		// TODO: should I do this here?
		RemoteStubCacheUtil.remove(new SessionServerDescription(
				_serverDescription.getServerIP(), _serverDescription
						.getServerPort(), sessionID));
		return null;
	}

}
