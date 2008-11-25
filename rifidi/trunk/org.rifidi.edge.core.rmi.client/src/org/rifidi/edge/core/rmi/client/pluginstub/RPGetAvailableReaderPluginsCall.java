/*
 *  GetAvaliableReaderPlugins.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.core.rmi.client.pluginstub;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.rifidi.edge.core.rmi.readerconnection.ReaderPluginManagerStub;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This command gets a list of the list the reader plugins that are currently
 * available on the server
 * 
 */
public class RPGetAvailableReaderPluginsCall extends
		ServerDescriptionBasedRemoteMethodCall<List<String>, RuntimeException> {

	/**
	 * This command gets a list of the list the reader plugins that are
	 * currently available on the server
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 */
	public RPGetAvailableReaderPluginsCall(ServerDescription serverDescription) {
		super(serverDescription);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.client.remotecall.AbstractRemoteMethodCall#
	 * performRemoteCall(java.rmi.Remote)
	 */
	@Override
	protected List<String> performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		ReaderPluginManagerStub stub = (ReaderPluginManagerStub) remoteObject;
		return stub.getAvailableReaderPlugins();
	}

}
