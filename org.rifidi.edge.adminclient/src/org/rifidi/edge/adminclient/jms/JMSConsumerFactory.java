package org.rifidi.edge.adminclient.jms;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection;

public class JMSConsumerFactory {

	private Log logger = LogFactory.getLog(JMSConsumerFactory.class);

	private static final String defaultURL = "tcp://localhost:61616";
	private static final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();

	private Connection connection;
	private Session session;

	private List<MessageConsumer> consumers = new ArrayList<MessageConsumer>();

	public JMSConsumerFactory(String url) {
		if (url != null && !url.isEmpty()) {
			logger.debug("Starting JMSFactory at " + url);
			connectionFactory.setBrokerURL(url);
		} else {
			logger.debug("Initializing JMSFactory at " + defaultURL);
			connectionFactory.setBrokerURL(defaultURL);
		}
	}

	public void stop() throws JMSException {
		logger.debug("Stopping JMSFactory");
		if (consumers.size() > 0)
			logger.debug("Closing remaining JMS MessageConsumers");
		for (MessageConsumer messageConsumer : consumers) {
			messageConsumer.close();
		}
		session.close();
		connection.stop();
		connection.close();
	}

	public void start() throws JMSException {
		logger.debug("Starting JMSFactory");
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}

	public MessageConsumer createConsumer(
			RemoteReaderConnection remoteReaderConnection) throws JMSException,
			RemoteException {
		Destination dest = session.createQueue(remoteReaderConnection
				.getMessageQueueName());
		MessageConsumer consumer = session.createConsumer(dest);
		consumers.add(consumer);
		return consumer;
	}

	public void deleteConsumer(MessageConsumer messageConsumer)
			throws JMSException {
		consumers.remove(messageConsumer);
		messageConsumer.close();
	}

}
