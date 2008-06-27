/*
 *  JMSServiceImpl.java
 *
 *  Created:	Jun 27, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.connection.jms;

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;


import org.rifidi.edge.core.connection.IReaderConnection;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class JMSServiceImpl implements JMSService {
	


	private Map<IReaderConnection, JMSMessageThread> map = new HashMap<IReaderConnection, JMSMessageThread>();
	private ConnectionFactory connectionFactory;
	
	public JMSServiceImpl(){
		ServiceRegistry.getInstance().service(this);
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.jms.service.JMSService#register(org.rifidi.edge.core.connection.IReaderConnection)
	 */
	@Override
	public boolean register(IReaderConnection connection) {
		JMSHelper jmsHelper = new JMSHelper();
		
		jmsHelper.initializeJMSQueue(connectionFactory, Integer.toString(connection.getSessionID()));
		
		JMSMessageThread jmsThread = new JMSMessageThread(connection.getSessionID(), connection.getAdapter() , jmsHelper);
		
		boolean retVal = jmsThread.start();
		
		if (retVal)
			map.put(connection, jmsThread);
		
		return retVal;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.jms.service.JMSService#unregister(org.rifidi.edge.core.connection.IReaderConnection)
	 */
	@Override
	public void unregister(IReaderConnection connection) {
		JMSMessageThread jmsThread = map.get(connection);
		
		if (jmsThread != null) 
			jmsThread.stop();
		
		map.remove(connection);
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
