package org.rifidi.edge.jms.service;

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.rifidi.edge.core.connection.IReaderConnection;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author jerry
 *
 */
public class JMSServiceImpl implements JMSService {
	
	public JMSServiceImpl(){
		ServiceRegistry.getInstance().service(this);
	}

	Map<IReaderConnection, JMSMessageThread> map = new HashMap<IReaderConnection, JMSMessageThread>();
	private ConnectionFactory connectionFactory;
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.jms.service.JMSService#register(org.rifidi.edge.core.connection.IReaderConnection)
	 */
	@Override
	public void register(IReaderConnection connection, String queueName) {
		// TODO Auto-generated method stub
		JMSHelper jmsHelper = new JMSHelper();
		map.put(connection, value);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.jms.service.JMSService#unregister(org.rifidi.edge.core.connection.IReaderConnection)
	 */
	@Override
	public void unregister(IReaderConnection connection) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * A method helper for the services injection framework.
	 * @param connectionFactory 
	 */
	@Inject
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
}
