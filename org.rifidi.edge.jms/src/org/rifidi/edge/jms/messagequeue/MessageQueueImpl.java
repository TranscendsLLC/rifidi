package org.rifidi.edge.jms.messagequeue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;

import org.rifidi.edge.core.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.messages.Message;

/**
 * Implementation of the MessageQueue using JMS as the underlying Queue. Allows
 * to store Messages on a JMS Queue.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class MessageQueueImpl implements MessageQueue {

	private Connection connection;
	private Session session;
	private Destination destination;
	private MessageProducer messageProducer;
	private String queueName;

	/**
	 * Create a new MessageQueue
	 * 
	 * @param queueName
	 *            the name used to create the queue
	 */
	public MessageQueueImpl(String queueName) {
		this.queueName = queueName;
	}

	/**
	 * Start the MessageQueue
	 * 
	 * @param connectionFactory
	 *            ConnectionFactory to the JMSBroker
	 * @throws JMSException
	 *             if there is a error starting the message queue or if the jms
	 *             broker couldn't be reached
	 */
	public void startMessageQueue(ConnectionFactory connectionFactory)
			throws JMSException {
		connection = connectionFactory.createConnection();
		connection.start();

		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		this.destination = session.createQueue(this.queueName);

		messageProducer = session.createProducer(destination);
	}

	/**
	 * Stop a previously started MessageQueue
	 * 
	 * @throws JMSException
	 */
	public void stopMessageQueue() throws JMSException {
		messageProducer.close();
		session.close();
		connection.stop();
		connection.close();
		((TemporaryQueue)destination).delete();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.messageQueue.MessageQueue#addMessage(org.rifidi.edge.core.readerplugin.messages.Message)
	 */
	@Override
	public void addMessage(Message message) throws RifidiMessageQueueException {
		// TODO Think if we want to use the protocol for this
		try {
			TextMessage textMessage = session
					.createTextMessage(message.toXML());
			textMessage.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
			textMessage.setJMSExpiration(1000);
			messageProducer.send(textMessage);
		} catch (JMSException e) {
			e.printStackTrace();
			throw new RifidiMessageQueueException("Could not send message", e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.messageQueue.MessageQueue#getName()
	 */
	@Override
	public String getName() {
		return queueName;
	}

}
