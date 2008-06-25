package org.rifidi.edge.jms.service;

import org.rifidi.edge.core.connection.IReaderConnection;

/**
 * @author jerry
 *
 */
public interface JMSService {
	public void register(IReaderConnection connection, String queueName);
	public void unregister(IReaderConnection connection);
}
