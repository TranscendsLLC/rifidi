package org.rifidi.edge.client.connections.registryservice.listeners;

import org.rifidi.edge.client.connections.remotereader.RemoteReader;

/**
 * A listner to add and remove events from the RemoteReader Registry
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface RemoteReaderConnectionRegistryListener {

	/**
	 * Event that is fired when a reader is added
	 * 
	 * @param remoteReader
	 *            the remote reader that was added
	 */
	public void readerAdded(RemoteReader remoteReader);

	/**
	 * Event that is fired when a reader is removed
	 * 
	 * @param remoteReader
	 *            The remote reader that was removed
	 */
	public void readerRemoved(RemoteReader remoteReader);

}
