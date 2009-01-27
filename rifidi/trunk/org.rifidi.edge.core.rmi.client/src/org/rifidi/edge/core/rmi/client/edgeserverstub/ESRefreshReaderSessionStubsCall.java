/*
 *  GetReaderSessionStubsCommand.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.core.rmi.client.edgeserverstub;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Set;

import org.rifidi.edge.core.rmi.api.readerconnection.EdgeServerStub;
import org.rifidi.edge.core.rmi.api.readerconnection.ReaderSessionStub;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionServerDescription;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.cache.util.RemoteStubCacheUtil;
import org.rifidi.rmi.utils.remotecall.ServerDescriptionBasedRemoteMethodCall;

/**
 * This remote method call gets the remote reader sessions and puts them in the
 * cache. It returns true if all the readersSessions that were asked for were
 * returned.
 * 
 * Normally remote objects are fetched from the RMI registry without explicit
 * calls. However, because many reader sessions might exist on the server when
 * the server first starts up, it would be inefficient for each of these to be
 * fetched individually. This RMI command object, although it breaks the
 * "invisibility" of the cache, is a necessary evil, to make reader session
 * retrieval more efficient
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ESRefreshReaderSessionStubsCall extends
		ServerDescriptionBasedRemoteMethodCall<Boolean, RuntimeException> {

	private Set<Long> readerSessionIDs;

	/**
	 * @param serverDescription
	 *            The description of the RMI stub
	 * @param readerSessionIDs
	 *            The IDs of the remote stubs to get
	 */
	public ESRefreshReaderSessionStubsCall(ServerDescription serverDescription,
			Set<Long> readerSessionIDs) {
		super(serverDescription);
		this.readerSessionIDs = readerSessionIDs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.client.remotecall.AbstractRemoteMethodCall#
	 * performRemoteCall(java.rmi.Remote)
	 */
	@Override
	protected Boolean performRemoteCall(Remote remoteObject)
			throws RemoteException, RuntimeException {
		EdgeServerStub edgeServerStub = (EdgeServerStub) remoteObject;
		HashMap<Long, ReaderSessionStub> remoteSessions = edgeServerStub
				.getReaderSessionStubs(readerSessionIDs);
		for (Long l : remoteSessions.keySet()) {
			ReaderSessionStub conn = remoteSessions.get(l);
			ServerDescription desc = new SessionServerDescription(
					_serverDescription.getServerIP(), _serverDescription
							.getServerPort(), l);
			RemoteStubCacheUtil.put(desc, (Remote) conn);
		}
		return remoteSessions.keySet().containsAll(readerSessionIDs);
	}

}
