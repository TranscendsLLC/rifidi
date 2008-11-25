/*
 *  EdgeServerConnectionState.java
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

import org.rifidi.edge.client.connections.exceptions.CannotCreateRemoteReaderException;
import org.rifidi.edge.client.connections.remotereader.RemoteReaderID;
import org.rifidi.edge.core.exceptions.RifidiException;
import org.rifidi.edge.core.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiReaderPluginXMLNotFoundException;
import org.rifidi.edge.core.rmi.client.pluginstub.valueobjects.FormAnnotationListWrapper;
import org.rifidi.edge.core.rmi.client.pluginstub.valueobjects.ReaderPluginWrapper;
import org.rifidi.edge.core.rmi.client.sessionstub.valueobjects.ReaderInfoWrapper;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;

/**
 * This abstract class represents the behavior that the EdgeServerConnection
 * exhibits depending on whether it is in the connected state or the
 * disconnected state
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class EdgeServerConnectionState {

	/**
	 * 
	 * @return Returns true if the EdgeServerConnection is in the connected
	 *         state (i.e. there is an active connection to the RMI server)
	 */
	public abstract boolean isConnected();

	/**
	 * Create a Remote Reader Session on the Edge Server
	 * 
	 * @param wrapper
	 *            The ReaderInfo
	 * @return The ID of the Remote reader
	 * @throws CannotCreateRemoteReaderException
	 *             If there was a problem when creating the reader
	 */
	public abstract RemoteReaderID createRemoteReader(ReaderInfoWrapper wrapper)
			throws CannotCreateRemoteReaderException;

	/**
	 * Destroys a reader session on the edge server
	 * 
	 * @param ID
	 *            The ID of the reader session to delete
	 */
	public abstract void destroyRemoteReader(RemoteReaderID ID);

	/**
	 * 
	 * @return The set of RemoteReadersIDs on this edge server
	 */
	public abstract Set<RemoteReaderID> getRemoteReaderIDs();

	/**
	 * 
	 * @return A list of readerInfo class names for each reader plugin available
	 *         on this edge server
	 */
	public abstract List<String> getAvailableReaderPlugins();

	/**
	 * 
	 * @param readerInfoClassName
	 *            The readerInfo class name for the plugin
	 * @return An XML annotation that contains the metainformation of the
	 *         ReaderInfo
	 * @throws RifidiReaderInfoNotFoundException
	 *             If the plugin cannot be found for the readerInfoClassName
	 */
	public abstract String getReaderInfoAnnotation(String readerInfoClassName)
			throws RifidiReaderInfoNotFoundException;

	/**
	 * This is an RMI call to the ReaderPluginManager to get a ReaderPluginXML
	 * for a certain readerInfo
	 * 
	 * @param readerInfoClassName
	 * @return
	 * @throws RifidiReaderPluginXMLNotFoundException
	 * @throws ServerUnavailable
	 */
	public abstract ReaderPluginWrapper getReaderPluginWrapper(
			String readerInfoClassName)
			throws RifidiReaderPluginXMLNotFoundException, ServerUnavailable;

	/**
	 * This is an RMI call to the ReaderPluginManager to get the Annotations for
	 * the properties of a reader
	 * 
	 * @param readerInfoClassName
	 * @return
	 * @throws ServerUnavailable
	 * @throws RifidiException
	 */
	public abstract FormAnnotationListWrapper getReaderPropertyAnnotations(
			String readerInfoClassName) throws ServerUnavailable,
			RifidiException;

	/**
	 * This is an RMI call to the ReaderPluginManager to get the Annotations for
	 * the commands of a reader
	 * 
	 * @param readerInfoClassName
	 * @return
	 * @throws ServerUnavailable
	 * @throws RifidiException
	 */
	public abstract FormAnnotationListWrapper getReaderCommandAnnotations(
			String readerInfoClassName) throws ServerUnavailable,
			RifidiException;

	/**
	 * This method attempts to move the EdgeServerConnection back to the
	 * connected state
	 */
	public abstract void resetConnection();

}
