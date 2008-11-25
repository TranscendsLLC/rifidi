package org.rifidi.edge.client.connections.registryservice;

import org.rifidi.edge.client.connections.registryservice.listeners.RemoteReaderConnectionRegistryListener;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.connections.remotereader.RemoteReaderID;

/**
 * This interface is an OSGi service for storing Remote Readers. All remote
 * readers are identified by a unique id that is assigned to it when the create
 * method is called
 * 
 * @author kyle
 * 
 */
public interface RemoteReaderConnectionRegistryService {

	/**
	 * Add a remote reader to the Registry
	 * 
	 * @param remoteReader
	 *            The remote reader to add
	 * @return The ID that identifies the remote reader
	 */
	public RemoteReaderID addReader(RemoteReader remoteReader);

	/**
	 * Return the RemoteReader associated with the given ID
	 * 
	 * @param remoteReaderID
	 * @return
	 */
	public RemoteReader getRemoteReader(RemoteReaderID remoteReaderID);

	/**
	 * Remote a remote reader from the registry
	 * 
	 * @param remoteReaderID
	 *            The ID of the remote reader to remove
	 */
	public void removeRemoteReader(RemoteReaderID remoteReaderID);

	/**
	 * Add a listener to listen to add and remove events
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addListener(RemoteReaderConnectionRegistryListener listener);

	/**
	 * Remove a listener that listens to add and remove events
	 * 
	 * @param listener
	 *            The Listener to remove
	 */
	public void removeListener(RemoteReaderConnectionRegistryListener listener);

}
