package org.rifidi.edge.jms.messagequeue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.rifidi.edge.core.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.messages.Message;

public class MessageQueueImpl implements MessageQueue {

	private Connection connection;
	private Session session;
	private Destination destination;
	private MessageProducer messageProducer;
	private String queueName;

	public MessageQueueImpl(String queueName) {
		this.queueName = queueName;
	}

	public void startMessageQueue(ConnectionFactory connectionFactory)
			throws JMSException {
		connection = connectionFactory.createConnection();
		connection.start();

		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		destination = session.createQueue(queueName);

		messageProducer = session.createProducer(destination);
	}
	
	public void stopMessageQueue() throws JMSException	{
		messageProducer.close();
		session.close();
		connection.stop();
		connection.close();
	}

	@Override
	public void addMessage(Message message) throws RifidiMessageQueueException {
		// TODO Think if we want to use the protocol for this
		try {
			TextMessage textMessage = session
					.createTextMessage(message.toXML());
			messageProducer.send(textMessage);
		} catch (JMSException e) {
			e.printStackTrace();
			throw new RifidiMessageQueueException("Could not send message", e);
		}

	}

	@Override
	public String getName() {
		return queueName;
	}

}
