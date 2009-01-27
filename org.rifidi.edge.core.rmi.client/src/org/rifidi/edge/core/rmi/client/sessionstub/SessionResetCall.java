/*
 *  SessionResetCall.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.core.rmi.client.sessionstub;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.edge.core.rmi.api.readerconnection.ReaderSessionStub;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * Moves the Session from the ERROR state to the Configured state
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SessionResetCall extends
		ServerDescriptionBasedRemoteMethodCall<Object, RuntimeException> {

	/**
	 * Moves the Session from the ERROR state to the Configured state
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 */
	public SessionResetCall(ServerDescription serverDescription) {
		super(serverDescription);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall
	 * (java.rmi.Remote)
	 */
	@Override
	protected Object performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		((ReaderSessionStub) remoteObject).resetReaderConnection();
		return null;
	}

}
