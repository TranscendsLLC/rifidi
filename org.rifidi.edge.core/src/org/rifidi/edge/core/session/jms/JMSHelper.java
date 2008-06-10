package org.rifidi.edge.core.session.jms;

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

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public MessageProducer getMessageProducer() {
		return messageProducer;
	}

	public void setMessageProducer(MessageProducer messageProducer) {
		this.messageProducer = messageProducer;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}

	
}
