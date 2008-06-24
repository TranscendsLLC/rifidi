package org.rifidi.edge.core.communication;

import java.io.IOException;

public interface ICommunicationConnection {
	
	public void send(Object msg) throws IOException;

	public Object recieve() throws IOException;

	public Object recieveNonBlocking() throws IOException;

	public Object recieveTimeOut(long mills) throws IOException;
}
