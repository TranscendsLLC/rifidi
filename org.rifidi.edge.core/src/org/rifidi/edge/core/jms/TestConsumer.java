package org.rifidi.edge.core.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;

public class TestConsumer implements MessageListener {
	private static final Log log = LogFactory.getLog(TestConsumer.class);

	private JmsTemplate template;
	private String myId = "foo";
	private Destination destination;
	private Connection connection;
	private Session session;
	private MessageConsumer consumer;

	public void start() {
//		String selector = "next = '" + myId + "'";
		try {
			ConnectionFactory factory = template.getConnectionFactory();
			connection = factory.createConnection();

			// we might be a reusable connection in spring
			// so lets only set the client ID once if its not set
			synchronized (connection) {
				if (connection.getClientID() == null) {
					connection.setClientID(myId);
				}
			}

			connection.start();

			session = connection
					.createSession(true, Session.CLIENT_ACKNOWLEDGE);
			consumer = session.createConsumer(destination, null, false);
			consumer.setMessageListener(this);
		} catch (JMSException ex) {
			log.error("", ex);
		}
	}

	public void stop() throws JMSException {
		if (consumer != null)
			consumer.close();
		if (session != null)
			session.close();
		if (connection != null)
			connection.close();
	}

	public void onMessage(Message message) {
		try {
			log.debug("message " + message);
			System.out.println("message " + message);
			message.acknowledge();
		} catch (JMSException e) {
			log.error("Failed to acknowledge: " + e, e);
		}
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
		if (template != null) {
			start();
		}
	}

	public void setTemplate(JmsTemplate template) {
		this.template = template;
		if (destination != null) {
			start();
		}
	}
}
