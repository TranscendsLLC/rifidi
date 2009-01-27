/*
 *  SessionGetStateCall.java
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
 * 
 * Returns the state of of the reader (CONFIGURED OK, BUSY, ERROR).
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SessionGetStateCall extends
		ServerDescriptionBasedRemoteMethodCall<String, RuntimeException> {

	/**
	 * Returns the state of of the reader (CONFIGURED OK, BUSY, ERROR).
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 */
	public SessionGetStateCall(ServerDescription serverDescription) {
		super(serverDescription);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall
	 * (java.rmi.Remote)
	 */
	@Override
	protected String performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		return ((ReaderSessionStub) remoteObject).getReaderState();
	}

}
