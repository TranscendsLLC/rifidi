/*
 *  StopCurCommandCall.java
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
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This command object stops the current command on the server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SessionStopCurCommandCall extends
		ServerDescriptionBasedRemoteMethodCall<Object, RuntimeException> {

	boolean force;
	Long commandID = null;

	/**
	 * Using this call, the server stops the current command
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 * @param force
	 *            If true, wait until the command finishes to stop. Else, force
	 *            it to shut down without waiting
	 */
	public SessionStopCurCommandCall(ServerDescription serverDescription,
			boolean force) {
		super(serverDescription);
		this.force = force;
	}

	/**
	 * Using this call, the server checks to make sure that the current command
	 * has the given ID before it is stopped
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 * @param force
	 *            If true, wait until the command finishes to stop. Else, force
	 *            it to shut down without waiting
	 * @param commandID
	 *            The ID of the current command
	 */
	public SessionStopCurCommandCall(ServerDescription serverDescription,
			boolean force, long commandID) {
		super(serverDescription);
		this.force = force;
		commandID = new Long(commandID);
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
		ReaderSessionStub stub = (ReaderSessionStub) remoteObject;
		if (null == commandID) {
			stub.stopCurCommand(force);
		} else {
			stub.stopCurCommand(force, commandID);
		}
		return null;
	}

}
