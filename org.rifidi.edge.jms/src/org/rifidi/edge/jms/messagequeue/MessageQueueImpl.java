package org.rifidi.edge.jms.messagequeue;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQDestination;
import org.rifidi.edge.core.api.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.api.messageQueue.MessageQueue;
import org.rifidi.edge.core.api.readerplugin.messages.Message;
import org.rifidi.edge.jms.messageservice.BrokerWrapper;

/**
 * Implementation of the MessageQueue using JMS as the underlying Queue. Allows
 * to store Messages on a JMS Queue.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class MessageQueueImpl implements MessageQueue {

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
	public void startMessageQueue(Connection connection) throws JMSException {

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
	}

	/**
	 * This method will remove the destination from the broker.
	 * 
	 * TODO: We may want to call this in some sort of a clean up thread that run periodically, because
	 * the destination will not be removed if the client has not disconnected
	 * from it.
	 * 
	 * @param brokerWrapper
	 * @throws Exception
	 */
	public void destroyDestination(BrokerWrapper brokerWrapper)
			throws Exception {
		brokerWrapper.destroyDestination((ActiveMQDestination) destination);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.messageQueue.MessageQueue#addMessage(org.rifidi.
	 * edge.core.readerplugin.messages.Message)
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
