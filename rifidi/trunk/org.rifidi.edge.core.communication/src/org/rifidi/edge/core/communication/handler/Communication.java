package org.rifidi.edge.core.communication.handler;

import java.io.IOException;

import org.rifidi.edge.core.communication.buffer.ConnectionBuffer;

public interface Communication {

	public ConnectionBuffer startCommunication() throws IOException;

	public void stopCommunication() throws IOException;

}
