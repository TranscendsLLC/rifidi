package org.rifidi.edge.core.communication;

import java.io.IOException;

/**
 * Connection to send and recieve Messages to and from a reader
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface Connection {

	/**
	 * Send the Message to the reader
	 * 
	 * @param o
	 *            Message(in the Format of the specific reader) to send
	 * @throws IOException
	 *             if there was a problem in the underlying communication layer
	 */
	public void sendMessage(Object o) throws IOException;

	/**
	 * Recieve a Message form the reader
	 * 
	 * @return the Message(in the Format of the specific reader) read form the
	 *         reader
	 * @throws IOException
	 *             if there was a problem in the underlying communication layer
	 */
	public Object receiveMessage() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 */
	public boolean isMessageAvailable() throws IOException;

	/**
	 * Receive a message
	 * 
	 * @param timeout
	 *            Time out in milliseconds.
	 * @return Object if successful, null if unsuccessful
	 * @throws IOException
	 */
	public Object receiveMessage(long timeout) throws IOException;
}
