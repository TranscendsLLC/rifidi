/*
 *  SessionExecuteCommandCall.java
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

import org.rifidi.edge.core.api.exceptions.RifidiException;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.rmi.api.readerconnection.ReaderSessionStub;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * 
 * This command object executes a command. It returns the ID of the command that
 * is executed
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SessionExecuteCommandCall extends
		ServerDescriptionBasedRemoteMethodCall<Long, RifidiException> {

	private CommandConfiguration commandConfiguration;

	/**
	 * This command object executes a command. It returns the ID of the command
	 * that is executed
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 * @param commandConfiguration
	 *            A description of the command to execute
	 */
	public SessionExecuteCommandCall(ServerDescription serverDescription,
			CommandConfiguration commandConfiguration) {
		super(serverDescription);
		this.commandConfiguration = commandConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall
	 * (java.rmi.Remote)
	 */
	@Override
	protected Long performRemoteCall(Remote remoteObject)
			throws RemoteException, RifidiException {
		return ((ReaderSessionStub) remoteObject)
				.executeCommand(commandConfiguration);
	}

}
