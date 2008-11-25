package org.rifidi.edge.client.connections.remotereader.listeners;

import javax.jms.Message;

import org.rifidi.edge.client.connections.remotereader.RemoteReader;

/**
 * A listner to messages from the reader session
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface ReaderMessageListener {
	/**
	 * Event that is fired when a new message is recieved
	 * 
	 * @param message
	 *            The message
	 * @param reader
	 *            The remote reader which sent the message
	 */
	public void onMessage(Message message, RemoteReader reader);
}
