package org.rifidi.edge.jms.service;

import org.rifidi.edge.core.connection.IReaderConnection;

/**
 * @author jerry
 *
 */
public interface JMSService {
	public boolean register(IReaderConnection connection);
	public void unregister(IReaderConnection connection);
}
