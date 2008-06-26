package org.rifidi.edge.core.communication.buffer;

import java.io.IOException;

import org.rifidi.edge.core.exception.readerConnection.RifidiIllegalOperationException;

public interface ConnectionBuffer {

	/**
	 * @param message
	 */
	public void send(Object message) throws RifidiIllegalOperationException,
			IOException;

	/**
	 * @return
	 */
	public Object recieve() throws RifidiIllegalOperationException, IOException;

	/**
	 * @param message
	 * @return
	 */
	public Object sendAndRecieve(Object message)
			throws RifidiIllegalOperationException, IOException;

}
