package org.rifidi.edge.core.connection.jms;

import org.rifidi.edge.core.connection.IReaderConnection;



/**
 * @author Jerry Maine - jerry@pramari.com
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
