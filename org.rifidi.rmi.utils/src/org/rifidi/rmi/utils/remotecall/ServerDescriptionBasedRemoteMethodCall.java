/*
 *  ServerDescriptionBasedRemoteMethodCall.java
 *
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.rmi.utils.remotecall;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.rmi.utils.cache.RemoteStubCache;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;

/**
 * This class was inspired by the three oreilly.com articles on RMI by William
 * Grosso.
 * 
 * The purpose of this class is to provide a base from which to make RMI command
 * objects. Most command objects should use this class
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public abstract class ServerDescriptionBasedRemoteMethodCall<T, E extends Exception>
		extends AbstractRemoteMethodCall<T, E> {
	protected ServerDescription _serverDescription;

	private Log logger = LogFactory
			.getLog(ServerDescriptionBasedRemoteMethodCall.class);

	/**
	 * Constructor.  
	 * 
	 * @param serverDescription
	 */
	public ServerDescriptionBasedRemoteMethodCall(
			ServerDescription serverDescription) {
		_serverDescription = serverDescription;
	}

	/**
	 * Make the remote method call.
	 */
	public T makeCall() throws ServerUnavailable, E {
		T returnValue = null;
		try {
			returnValue = super.makeCall();
		} finally {
			RemoteStubCache.noLongerUsingStubToRemoteObject(_serverDescription);
		}
		return returnValue;
	}

	protected Remote getRemoteObject() throws ServerUnavailable {
		try {
			Remote stub = RemoteStubCache
					.getStubToRemoteObject(_serverDescription);
			return stub;
		} catch (ServerUnavailable serverUnavailable) {
			logger.error("Can't find stub for server " + _serverDescription);
			throw serverUnavailable;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.rifidi.rmi.utils.remotecall.AbstractRemoteMethodCall#remoteExceptionOccurred(java.rmi.RemoteException)
	 */
	@Override
	protected void remoteExceptionOccurred(RemoteException remoteException) {
		logger.warn("Remote Exception: " + remoteException.getMessage());
		RemoteStubCache.removeStubFromCache(_serverDescription);
	}
}
