package org.rifidi.edge.core.communication;

import java.io.IOException;

public interface Connection {
	// TODO Change Object to Message
	public void sendMessage(Object o) throws IOException;

	// TODO Change Object to Message
	public Object recieveMessage() throws IOException;

	public boolean isMessageAvailable() throws IOException;
}
