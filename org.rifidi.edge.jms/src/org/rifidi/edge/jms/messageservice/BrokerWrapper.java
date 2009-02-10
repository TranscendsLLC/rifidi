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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class wraps the BrokerService to only expose functionality that is
 * needed
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class BrokerWrapper {

	private Log logger = LogFactory.getLog(BrokerWrapper.class);
	private BrokerService broker;
	private ConnectionFactory connectionFactory;

	/**
	 * Emptry Constructor for spring
	 */
	public BrokerWrapper() {
	}

	/**
	 * Setter method for spring to call
	 * 
	 * @param service
	 */
	public void setBrokerService(BrokerService service) {
		if (service == null) {
			throw new NullPointerException("BrokerService cannot be null");
		}
		this.broker = service;
	}

	/**
	 * Setter method for spring to call
	 * 
	 * @param factory
	 */
	public void setConnectionFactory(ConnectionFactory factory) {
		if (factory == null) {
			throw new NullPointerException("Factory cannot be null");
		}
		this.connectionFactory = factory;
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
		if (broker != null) {
			broker.removeDestination(destination);
		} else {
			logger.error("broker is null");
		}
	}

	public ConnectionFactory getConnectionFactory() {
		if (connectionFactory == null) {
			logger
					.error("ConnectionFactory is null.  BrokerWrapper was not initialized properly");
			throw new NullPointerException();
		}
		return this.connectionFactory;
	}

}
