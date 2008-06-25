package org.rifidi.edge.jms.service;

import org.rifidi.edge.core.connection.reader.IReaderConnection;

/**
 * @author jerry
 *
 */
public interface JMSService {
	
	/**
	 * Registers an IReaderConnection with this service
	 * @param connection
	 * @return True if successful. False if unsuccessful.
	 */
	public boolean register(IReaderConnection connection);
	
	/**
	 * Unregisters an IReaderConnection with this service.
	 * @param connection
	 */
	public void unregister(IReaderConnection connection);
}
