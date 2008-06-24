package org.rifidi.edge.core.communication;

import java.io.IOException;


public interface ICommunicationConnection {
	public void send(Object msg) throws IOException;
	public Object recieve() throws IOException;
}
