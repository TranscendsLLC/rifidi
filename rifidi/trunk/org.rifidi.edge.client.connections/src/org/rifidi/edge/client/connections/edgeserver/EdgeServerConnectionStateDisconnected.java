/*
 *  EdgeServerConnectionStateDisconnected.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.client.connections.edgeserver;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.client.connections.remotereader.RemoteReaderID;
import org.rifidi.edge.core.api.exceptions.RifidiException;
import org.rifidi.edge.core.api.exceptions.RifidiReaderPluginXMLNotFoundException;
import org.rifidi.edge.core.rmi.client.edgeserverstub.ESGetAllReaderSessionIDsCall;
import org.rifidi.edge.core.rmi.client.pluginstub.valueobjects.FormAnnotationListWrapper;
import org.rifidi.edge.core.rmi.client.pluginstub.valueobjects.ReaderPluginWrapper;
import org.rifidi.edge.core.rmi.client.sessionstub.valueobjects.ReaderInfoWrapper;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;

/**
 * This class represents the behavior of the EdgeServerConnection when it is in
 * the disconnected state
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EdgeServerConnectionStateDisconnected extends
		EdgeServerConnectionState {

	private Log logger = LogFactory
			.getLog(EdgeServerConnectionStateDisconnected.class);

	private EdgeServerConnection esConnection;

	/**
	 * @param edgeServerConnection
	 */
	public EdgeServerConnectionStateDisconnected(
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
	public RemoteReaderID createRemoteReader(ReaderInfoWrapper wrapper) {
		logger.debug("Cannot create remote reader in disconnected state");
		return null;
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
		logger.debug("Cannot destroy remote reader in disconnected state");
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
		logger
				.debug("Cannot get available reader plugins in disconnected state");
		return null;
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
		logger
				.debug("Cannot get reader command annotations in disconnected state");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.connections.edgeserver.EdgeServerConnectionState
	 * #getReaderInfoAnnotation(java.lang.String)
	 */
	@Override
	public String getReaderInfoAnnotation(String readerInfoClassName) {
		logger.debug("Cannot get reader info annotation in disconnected state");
		return null;
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
		logger.debug("Cannot get reader plugin wrapper in disconnected state");
		return null;
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
		logger
				.debug("Cannot get reader property annotations in disconnected state");
		return null;
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
		logger.debug("Cannot get remote reader IDs in disconnected state");
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
		return false;
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

		// get the list of readers on the server
		ESGetAllReaderSessionIDsCall command = new ESGetAllReaderSessionIDsCall(
				esConnection.edggeServerSD);

		try {
			command.makeCall();
			esConnection.transitionToConnected();
		} catch (ServerUnavailable e) {
			logger.error("Cannot connect to server", e);
		}

	}

}
