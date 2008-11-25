/*
 *  SessionCommandStatusCall.java
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
import org.rifidi.edge.core.rmi.readerconnection.returnobjects.CommandInfo;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This command object returns the CommandInfo of a command. If no command id is
 * supplied, it returns the information for the current command
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SessionCommandStatusCall extends
		ServerDescriptionBasedRemoteMethodCall<CommandInfo, RuntimeException> {

	private Long commandID = null;

	/**
	 * 
	 * Returns the CommmandInfo for the current command
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 */
	public SessionCommandStatusCall(ServerDescription serverDescription) {
		super(serverDescription);
	}

	/**
	 * 
	 * Return the CommandInfo for the command with the given ID
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 * @param commandID
	 *            The ID of the particular command to search for
	 */
	public SessionCommandStatusCall(ServerDescription serverDescription,
			Long commandID) {
		super(serverDescription);
		this.commandID = commandID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall
	 * (java.rmi.Remote)
	 */
	@Override
	protected CommandInfo performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderSessionStub stub = (ReaderSessionStub) remoteObject;
		if (null == commandID) {
			return stub.commandStatus();
		} else {
			return stub.commandStatus(commandID);
		}
	}

}
