/*
 *  JMSHelper.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.jms.service.helper;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * This is a helper holding all JMS specific instances for the Queue
 * 
 * @author andreas
 * 
 */
public class JMSHelper {

	private boolean isInitialized;

	private Connection connection;
	private Session session;
	private Destination destination;
	private MessageProducer messageProducer;

	/**
	 * Initialize the JMS Helper
	 * 
	 * @param connectionFactory
	 *            The JMS Connection Factory
	 * @param queueName
	 *            The name of a queue in string form
	 * @return
	 */
	public boolean initializeJMSQueue(ConnectionFactory connectionFactory,
			String queueName) {
		try {
			connection = connectionFactory.createConnection();
			connection.start();

			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			destination = session.createQueue(queueName);

			messageProducer = session.createProducer(destination);

			isInitialized = true;
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return isInitialized;
	}

	/**
	 * @return A JMS Connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * @param connection
	 *            A JMS Connection
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * @return A JMS Session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @param session
	 *            A JMS Session
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * @return A JMS Destination
	 */
	public Destination getDestination() {
		return destination;
	}

	/**
	 * @param destination
	 *            A JMS Destination
	 */
	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	/**
	 * @return A JMS MessageProducer
	 */
	public MessageProducer getMessageProducer() {
		return messageProducer;
	}

	/**
	 * @param messageProducer
	 *            A JMS MessageProducer
	 */
	public void setMessageProducer(MessageProducer messageProducer) {
		this.messageProducer = messageProducer;
	}

	/**
	 * @return Is this helper initialized?
	 */
	public boolean isInitialized() {
		return isInitialized;
	}

	/**
	 * Set if this helper is initialized or not.
	 * 
	 * @param isInitialized
	 */
	public void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}

}
