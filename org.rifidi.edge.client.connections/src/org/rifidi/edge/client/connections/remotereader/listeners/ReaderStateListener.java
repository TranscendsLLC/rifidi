package org.rifidi.edge.client.connections.remotereader.listeners;

import org.rifidi.edge.client.connections.remotereader.RemoteReaderID;

/**
 * A listener to session state changes of a reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface ReaderStateListener {
	/**
	 * Event that is fired when the session changes state
	 * 
	 * @param readerID
	 *            The ID of the reader that has changed states
	 * @param newState
	 *            The new state the reader has changed to
	 */
	public void readerStateChanged(RemoteReaderID readerID, String newState);
}
