/*
 *  RemoteStubCache.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.rmi.utils.cache;

import java.rmi.Remote;
import java.util.concurrent.ConcurrentHashMap;

import org.rifidi.rmi.utils.exceptions.ServerUnavailable;

/**
 * This class was inspired by the three oreilly.com articles on RMI by William
 * Grosso.
 * 
 * This class acts as a Cache for remote objects to increase performance, so
 * that remote objects are not obtained from the RMI registry every time they
 * are used.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RemoteStubCache {

	// private static Log logger = LogFactory.getLog(RemoteStubCache.class);

	/**
	 * The cache
	 */
	private static ConcurrentHashMap<ServerDescription, Remote> _serverDescriptionsToStubs = new ConcurrentHashMap<ServerDescription, Remote>();

	/**
	 * Gets a Remote Object Stub with given Server Description. Retrieves it
	 * from cache if available, if not, looks it up on the RMI Registry
	 * 
	 * @param serverDescription
	 *            The description of the remote object to retrieve
	 * @return A Remote Object Stub
	 * @throws ServerUnavailable
	 *             if there is a connection problem
	 */
	public static Remote getStubToRemoteObject(
			ServerDescription serverDescription) throws ServerUnavailable {
		Remote returnValue = _serverDescriptionsToStubs.get(serverDescription);
		if (null == returnValue) {
			returnValue = serverDescription.getStub();
			if (null != returnValue) {
				_serverDescriptionsToStubs.put(serverDescription, returnValue);
			} else {
				throw new ServerUnavailable();
			}
		}
		return returnValue;

	}

	/**
	 * 
	 * @param serverDescription
	 */
	public static void noLongerUsingStubToRemoteObject(
			ServerDescription serverDescription) {
		/*
		 * Needs to be implemented if we take cleaning up the cache seriously.
		 */
	}

	/**
	 * Remote a remote object from cache
	 * 
	 * @param serverDescription
	 */
	public static void removeStubFromCache(ServerDescription serverDescription) {
		_serverDescriptionsToStubs.remove(serverDescription);
	}

}
