/*
 *  SessionGetSessionPropsCall.java
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

import org.rifidi.edge.core.rmi.readerconnection.ReaderSessionStub;
import org.rifidi.edge.core.rmi.readerconnection.returnobjects.ReaderSessionProperties;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This command object gets some name-value pairs that do not change over the
 * lifetime of the session
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SessionGetSessionPropsCall
		extends
		ServerDescriptionBasedRemoteMethodCall<ReaderSessionProperties, RuntimeException> {

	/**
	 * This command object gets some name-value pairs that do not change over
	 * the lifetime of the session
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 */
	public SessionGetSessionPropsCall(ServerDescription serverDescription) {
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
	protected ReaderSessionProperties performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		return ((ReaderSessionStub) remoteObject).getSessionProperties();
	}

}
