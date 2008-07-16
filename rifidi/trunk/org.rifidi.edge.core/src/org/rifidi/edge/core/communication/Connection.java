package org.rifidi.edge.core.communication;

import java.io.IOException;

public interface Connection {
	// TODO Change Object to Message
	public void sendMessage(Object o) throws IOException;

	// TODO Change Object to Message
	public Object receiveMessage() throws IOException;

	public boolean isMessageAvailable() throws IOException;

	
	/**
	 * Receive a message 
	 * @param timeout Time out in milliseconds.
	 * @return Object if successful, null if unsuccessful
	 * @throws IOException
	 */
	Object receiveMessage(long timeout) throws IOException;
}
