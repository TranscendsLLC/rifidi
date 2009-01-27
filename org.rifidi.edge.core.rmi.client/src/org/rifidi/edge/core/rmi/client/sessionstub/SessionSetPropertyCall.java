/*
 *  SessionSetPropertyCall.java
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
import org.rifidi.edge.core.api.readerplugin.property.PropertyConfiguration;
import org.rifidi.edge.core.rmi.api.readerconnection.ReaderSessionStub;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This command object sets one or more reader properties. It returns the values
 * that were set
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SessionSetPropertyCall
		extends
		ServerDescriptionBasedRemoteMethodCall<PropertyConfiguration, RifidiException> {

	private PropertyConfiguration propertyConfiguration;

	/**
	 * This command object sets one or more reader properties It returns the
	 * values that were set
	 * 
	 * @param serverDescription
	 *            The description of the RMI stub
	 * @param propertyConfiguration
	 *            A set of properties and their values to set on the server
	 */
	public SessionSetPropertyCall(ServerDescription serverDescription,
			PropertyConfiguration propertyConfiguration) {
		super(serverDescription);
		this.propertyConfiguration = propertyConfiguration;
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
		return ((ReaderSessionStub) remoteObject)
				.setProperty(propertyConfiguration);
	}

}
