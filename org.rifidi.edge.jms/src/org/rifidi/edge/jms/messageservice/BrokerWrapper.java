/*
 *  BrokerWrapper.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.jms.messageservice;

import javax.jms.ConnectionFactory;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;

/**
 * This class wraps the BrokerService to only expose functionality that is
 * needed
 * 
 * @author kyle
 * 
 */
public class BrokerWrapper {

	private BrokerService broker;
	private ConnectionFactory connectionFactory;

	public BrokerWrapper(BrokerService broker, ConnectionFactory connectionFactory) {
		this.broker = broker;
		this.connectionFactory = connectionFactory;
	}

	/**
	 * This method destroys a destination using the BrokerService from ActiveMQ
	 * 
	 * @param destination
	 *            The destination to destroy
	 * @throws Exception
	 *             is thrown if there is a problem removing the destination
	 */
	public void destroyDestination(ActiveMQDestination destination)
			throws Exception {
		broker.removeDestination(destination);
	}
	
	public ConnectionFactory getConnectionFactory(){
		return this.connectionFactory;
	}

}
