package org.rifidi.edge.core.communication;


public interface ICommunicationConnection {
	public void send(Object msg) throws Exception;
	public Object recieve() throws Exception;
}
