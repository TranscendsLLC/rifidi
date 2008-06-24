package org.rifidi.edge.core.communication;

import java.io.IOException;


/**
 * @author jerry
 *
 */
public interface ICommunicationConnection {
	
	/**
	 * Used to send an message to a reader
	 * @param msg Message to be sent that can be understood by the adapters Protocol class
	 * @throws IOException
	 */
	public void send(Object msg) throws IOException;
	
	/**
	 * Receive a message from a reader
	 * @return
	 * @throws IOException
	 */
	public Object receive() throws IOException;
	
	/**
	 * @return
	 * @throws IOException
	 */
	public Object receiveNonBlocking() throws IOException;
	
	/**
	 * @param mills
	 * @return
	 * @throws IOException
	 */
	public Object receiveTimeOut(long mills) throws IOException;
}
