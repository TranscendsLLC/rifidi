/*
 *  SessionGetPropertyCall.java
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
import java.util.Set;

import org.rifidi.edge.core.api.exceptions.RifidiException;
import org.rifidi.edge.core.api.readerplugin.property.PropertyConfiguration;
import org.rifidi.edge.core.rmi.api.readerconnection.ReaderSessionStub;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * 
 * This call gets the PropertyConfiguration for a set of property names
 * 
 * @author Kyle Neumeier - kyle@pramari.coms
 * 
 */
public class SessionGetPropertyCall
		extends
		ServerDescriptionBasedRemoteMethodCall<PropertyConfiguration, RifidiException> {

	private Set<String> propertyNames;

	/**
	 * This call gets the PropertyConfiguration for a set of property names
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 * @param propertyNames
	 *            A list of property names to get the value of
	 */
	public SessionGetPropertyCall(ServerDescription serverDescription,
			Set<String> propertyNames) {
		super(serverDescription);
		this.propertyNames = propertyNames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#performRemoteCall
	 * (java.rmi.Remote)
	 */
	@Override
	protected PropertyConfiguration performRemoteCall(Remote remoteObject)
			throws RemoteException, RifidiException {
		return ((ReaderSessionStub) remoteObject).getProperty(propertyNames);
	}

}
