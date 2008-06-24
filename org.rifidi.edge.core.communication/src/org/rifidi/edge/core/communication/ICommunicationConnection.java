package org.rifidi.edge.core.communication;

import java.io.IOException;


/**
 * @author jerry
 *
 */
public interface ICommunicationConnection {
	/**
	 * @param msg
	 * @throws IOException
	 */
	public void send(Object msg) throws IOException;
	/**
	 * @return
	 * @throws IOException
	 */
	public Object recieve() throws IOException;
	/**
	 * @return
	 * @throws IOException
	 */
	public Object recieveNonBlocking() throws IOException;
	/**
	 * @param mills
	 * @return
	 * @throws IOException
	 */
	public Object recieveTimeOut(long mills) throws IOException;
}
