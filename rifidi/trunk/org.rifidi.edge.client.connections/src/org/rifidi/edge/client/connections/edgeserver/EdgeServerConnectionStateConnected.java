/*
 *  EdgeServerConnectionStateConnected.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.client.connections.edgeserver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.client.connections.exceptions.CannotCreateRemoteReaderException;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.connections.remotereader.RemoteReaderID;
import org.rifidi.edge.core.exceptions.RifidiException;
import org.rifidi.edge.core.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiReaderPluginXMLNotFoundException;
import org.rifidi.edge.core.rmi.client.edgeserverstub.ESCreateReaderSessionCall;
import org.rifidi.edge.core.rmi.client.edgeserverstub.ESDeleteReaderSessionCall;
import org.rifidi.edge.core.rmi.client.edgeserverstub.ESGetAllReaderSessionIDsCall;
import org.rifidi.edge.core.rmi.client.edgeserverstub.ESRefreshReaderSessionStubsCall;
import org.rifidi.edge.core.rmi.client.pluginstub.RPGetAvailableReaderPluginsCall;
import org.rifidi.edge.core.rmi.client.pluginstub.RPGetCommandAnnotationsCall;
import org.rifidi.edge.core.rmi.client.pluginstub.RPGetPropertyAnnotationsCall;
import org.rifidi.edge.core.rmi.client.pluginstub.RPGetReaderInfoAnnotationCall;
import org.rifidi.edge.core.rmi.client.pluginstub.RPGetReaderPluginXMLCall;
import org.rifidi.edge.core.rmi.client.pluginstub.valueobjects.FormAnnotationListWrapper;
import org.rifidi.edge.core.rmi.client.pluginstub.valueobjects.ReaderPluginWrapper;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionGetSessionPropsCall;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionServerDescription;
import org.rifidi.edge.core.rmi.client.sessionstub.valueobjects.ReaderInfoWrapper;
import org.rifidi.edge.core.rmi.readerconnection.returnobjects.ReaderSessionProperties;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;

/**
 * This class represents the behavior of the EdgeServerConnection when it is in
 * the connected state
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EdgeServerConnectionStateConnected extends
		EdgeServerConnectionState {

	/**
	 * Logger for this class
	 */
	private Log logger = LogFactory
			.getLog(EdgeServerConnectionStateConnected.class);

	private EdgeServerConnection esConnection;

	private boolean isUpdating = false;

	private long lastUpdateTime = 0;

	/**
	 * @param edgeServerConnection
	 */
	public EdgeServerConnectionStateConnected(
			EdgeServerConnection edgeServerConnection) {
		this.esConnection = edgeServerConnection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.connections.edgeserver.EdgeServerConnectionState
	 * #createRemoteReader
	 * (org.rifidi.edge.core.rmi.client.sessionstub.valueobjects
	 * .ReaderInfoWrapper)
	 */
	@Override
	public RemoteReaderID createRemoteReader(ReaderInfoWrapper wrapper)
			throws CannotCreateRemoteReaderException {
		ESCreateReaderSessionCall command = new ESCreateReaderSessionCall(
				esConnection.edggeServerSD, wrapper);
		RemoteReaderID id = null;
		try {
			long sessionID = command.makeCall();
			id = new RemoteReaderID(this.esConnection.edgeServerConnectionID,
					sessionID);

			SessionGetSessionPropsCall sessionPropsCall = new SessionGetSessionPropsCall(
					new SessionServerDescription(esConnection.edggeServerSD
							.getServerIP(), esConnection.edggeServerSD
							.getServerPort(), sessionID));
			ReaderSessionProperties props = sessionPropsCall.makeCall();

			makeRemoteReader(id, props);
			return id;
		} catch (RifidiReaderInfoNotFoundException ex) {
			throw new CannotCreateRemoteReaderException(ex);
		} catch (ServerUnavailable e) {
			esConnection.transitionToDisconnected();
			throw new CannotCreateRemoteReaderException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.connections.edgeserver.EdgeServerConnectionState
	 * #destroyRemoteReader(java.lang.Long)
	 */
	@Override
	public void destroyRemoteReader(RemoteReaderID ID) {
		ESDeleteReaderSessionCall command = new ESDeleteReaderSessionCall(
				esConnection.edggeServerSD, ID.getRemoteSessionID());
		cleanUpReaderSession(ID);
		try {
			command.makeCall();

		} catch (ServerUnavailable e) {
			logger.error(e);
		}
		esConnection.remoteReaderConnectionRegistryService
				.removeRemoteReader(ID);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.connections.edgeserver.EdgeServerConnectionState
	 * #getAvailableReaderPlugins()
	 */
	@Override
	public List<String> getAvailableReaderPlugins() {
		RPGetAvailableReaderPluginsCall command = new RPGetAvailableReaderPluginsCall(
				esConnection.readerPluginManagerSD);
		try {
			return command.makeCall();
		} catch (ServerUnavailable e) {
			logger.error("ServerUnavailable", e);
			esConnection.transitionToDisconnected();
			return new ArrayList<String>();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.connections.edgeserver.EdgeServerConnectionState
	 * #getReaderCommandAnnotations(java.lang.String)
	 */
	@Override
	public FormAnnotationListWrapper getReaderCommandAnnotations(
			String readerInfoClassName) throws ServerUnavailable,
			RifidiException {
		RPGetCommandAnnotationsCall call = new RPGetCommandAnnotationsCall(
				esConnection.readerPluginManagerSD, readerInfoClassName);
		try {
			return call.makeCall();
		} catch (ServerUnavailable e) {
			esConnection.transitionToDisconnected();
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.connections.edgeserver.EdgeServerConnectionState
	 * #getReaderInfoAnnotation(java.lang.String)
	 */
	@Override
	public String getReaderInfoAnnotation(String readerInfoClassName)
			throws RifidiReaderInfoNotFoundException {
		RPGetReaderInfoAnnotationCall call = new RPGetReaderInfoAnnotationCall(
				esConnection.readerPluginManagerSD, readerInfoClassName);
		try {
			return call.makeCall();
		} catch (ServerUnavailable e) {
			logger.error(e);
			esConnection.transitionToDisconnected();
			return "";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.connections.edgeserver.EdgeServerConnectionState
	 * #getReaderPluginWrapper()
	 */
	@Override
	public ReaderPluginWrapper getReaderPluginWrapper(String readerInfoClassName)
			throws RifidiReaderPluginXMLNotFoundException, ServerUnavailable {
		RPGetReaderPluginXMLCall call = new RPGetReaderPluginXMLCall(
				esConnection.readerPluginManagerSD, readerInfoClassName);
		try {
			return call.makeCall();
		} catch (ServerUnavailable e) {
			logger.error("Server Unavailable ", e);
			esConnection.transitionToDisconnected();
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.connections.edgeserver.EdgeServerConnectionState
	 * #getReaderPropertyAnnotations(java.lang.String)
	 */
	@Override
	public FormAnnotationListWrapper getReaderPropertyAnnotations(
			String readerInfoClassName) throws ServerUnavailable,
			RifidiException {
		RPGetPropertyAnnotationsCall call = new RPGetPropertyAnnotationsCall(
				esConnection.readerPluginManagerSD, readerInfoClassName);
		try {
			return call.makeCall();
		} catch (ServerUnavailable e) {
			logger.error("Server Unavailable", e);
			esConnection.transitionToDisconnected();
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.connections.edgeserver.EdgeServerConnectionState
	 * #getRemoteReaderIDs()
	 */
	@Override
	public Set<RemoteReaderID> getRemoteReaderIDs() {
		update();
		return esConnection.readerIDs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.connections.edgeserver.EdgeServerConnectionState
	 * #isConnected()
	 */
	@Override
	public boolean isConnected() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.connections.edgeserver.EdgeServerConnectionState
	 * #resetConnection()
	 */
	@Override
	public void resetConnection() {
		logger.debug("Already in the connected state");

	}

	private void cleanUpReaderSession(RemoteReaderID id) {
		esConnection.readerIDs.remove(id);
		esConnection.remoteReaderConnectionRegistryService.getRemoteReader(id)
				.destroy();
	}

	/**
	 * update() synchronizes the list of readers on the server with the list of
	 * remoteReaders that are children of this connection. This method is
	 * protected by the isUpdating variable which gaurds to make sure that
	 * update() is not called recursively for performance and correctness
	 * reasons. Recursive calls could happen if a listener of the
	 * remoteReaderRegistryService calls something that would trigger this
	 * method when a reader is added or removed. It is ok to disallow recursive
	 * calls because it is merely synchronizing this connection's reader with
	 * the server's.
	 */
	private synchronized void update() {
		if (!isUpdating) {
			long curTime = System.currentTimeMillis();
			if ((curTime - lastUpdateTime) < 1000) {
				return;
			}
			lastUpdateTime = curTime;
			try {
				isUpdating = true;

				// get the list of readers on the server
				ESGetAllReaderSessionIDsCall command = new ESGetAllReaderSessionIDsCall(
						esConnection.edggeServerSD);

				Set<Long> sessionIDs = command.makeCall();

				// All the sessions that no long exist on the server
				Set<Long> nonExistingSessions = new HashSet<Long>();
				for (RemoteReaderID ids : esConnection.readerIDs) {
					nonExistingSessions.add(ids.getRemoteSessionID());
				}
				nonExistingSessions.removeAll(sessionIDs);

				Set<Long> newlyFoundSessions = new HashSet<Long>();
				newlyFoundSessions.addAll(sessionIDs);
				for (RemoteReaderID ids : esConnection.readerIDs) {
					newlyFoundSessions.remove(ids.getRemoteSessionID());
				}

				/* REMOVE OLD READERS */
				for (Long id : nonExistingSessions) {
					RemoteReaderID rrID = new RemoteReaderID(
							this.esConnection.edgeServerConnectionID, id);
					cleanUpReaderSession(rrID);
					esConnection.remoteReaderConnectionRegistryService
							.removeRemoteReader(rrID);
				}

				/* ADD NEW READERS */

				// refresh Stub cache for performance reasons
				ESRefreshReaderSessionStubsCall refreshCommand = new ESRefreshReaderSessionStubsCall(
						esConnection.edggeServerSD, newlyFoundSessions);
				refreshCommand.makeCall();

				// TODO: It might be useful to add a call to the EdgeServerStub
				// to allow us to get all the ReaderSessionProperties at once
				for (Long id : newlyFoundSessions) {

					try {
						RemoteReaderID rrID = new RemoteReaderID(
								this.esConnection.edgeServerConnectionID, id);
						SessionServerDescription sd = new SessionServerDescription(
								esConnection.edggeServerSD.getServerIP(),
								esConnection.edggeServerSD.getServerPort(), id);

						SessionGetSessionPropsCall getprops = new SessionGetSessionPropsCall(
								sd);
						ReaderSessionProperties rsProps = getprops.makeCall();

						makeRemoteReader(rrID, rsProps);
					} catch (ServerUnavailable e) {
						logger.error("Reader Session Stub " + id
								+ " is not available ", e);
					}
				}

			} catch (ServerUnavailable e) {
				esConnection.transitionToDisconnected();
			}
			isUpdating = false;
		}

	}

	private void makeRemoteReader(RemoteReaderID id,
			ReaderSessionProperties rsProps) {

		RemoteReader rr = new RemoteReader(esConnection.jmsFactory, rsProps,
				id, esConnection.edgeServerConnectionID,
				esConnection.edggeServerSD);

		esConnection.readerIDs.add(id);
		esConnection.remoteReaderConnectionRegistryService.addReader(rr);

	}

}
