package org.rifidi.edge.core.communication.buffer;

import java.io.IOException;


/**
 * @author jerry
 *
 */
public interface CommunicationBuffer {
	
	/**
	 * Used to send an message to a reader
	 * @param msg Message to be sent that can be understood by the adapters Protocol class
	 * @throws IOException
	 */
	public void send(Object msg) throws IOException;
	
	/**
	 * Receive a message from a reader.
	 * Will block and wait for a message if there is no messages on the buffer.
	 * @return Message
	 * @throws IOException
	 */
	public Object receive() throws IOException;
	
	/**
	 * Receive a message from a reader.
	 * Nonblocking receive method.
	 * @return Message
	 * @throws IOException
	 */
	public Object receiveNonBlocking() throws IOException;
	
	/**
	 * Receive a message from a reader.
	 * Will block and wait for a message or time out and return null 
	 * @param mills Milliseconds to time out.
	 * @return Message
	 * @throws IOException
	 */
	public Object receiveTimeOut(long mills) throws IOException;
	
	/*TODO: Consider adding a method that will dump all available messages on
	 *		a Collection or list and return. (blocking and or non-blocking).
	 */
}
