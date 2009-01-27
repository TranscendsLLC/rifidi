/*
 *  GetAvailableProperties.java
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

import org.rifidi.edge.core.rmi.api.readerconnection.ReaderPluginManagerStub;
import org.rifidi.edge.core.rmi.api.readerconnection.exceptions.RifidiPluginDoesNotExistException;
import org.rifidi.edge.core.rmi.api.readerconnection.returnobjects.CommandGroupMap;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This command object gets a list of the available properties and which groups
 * they are a member of
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RPGetAvailablePropertiesCall
		extends
		ServerDescriptionBasedRemoteMethodCall<List<CommandGroupMap>, RifidiPluginDoesNotExistException> {

	private String readerInfoClassName;

	/**
	 * 
	 * This command object gets a list of the available properties and which
	 * groups they are a member of
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 * @param readerInfoClassName
	 *            The qualified class name of the readerInfoClass for the reader
	 *            plugin
	 */
	public RPGetAvailablePropertiesCall(ServerDescription serverDescription,
			String readerInfoClassName) {
		super(serverDescription);
		this.readerInfoClassName = readerInfoClassName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.client.remotecall.AbstractRemoteMethodCall#
	 * performRemoteCall(java.rmi.Remote)
	 */
	@Override
	protected List<CommandGroupMap> performRemoteCall(Remote remoteObject)
			throws RemoteException, RifidiPluginDoesNotExistException {
		ReaderPluginManagerStub stub = (ReaderPluginManagerStub) remoteObject;
		return stub.getAvailableProperties(readerInfoClassName);
	}

}
