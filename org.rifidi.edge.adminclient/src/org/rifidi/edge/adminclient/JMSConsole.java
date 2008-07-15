package org.rifidi.edge.adminclient;

import javax.jms.Connection;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JMSConsole implements Runnable {
	private Log logger = LogFactory.getLog(JMSConsole.class);
	
	private final String connectionURL = "tcp://localhost:61616";
	
	private static final String defaultURL = "tcp://localhost:61616";
	private static final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();

	private Connection connection;
	private Session session;
	
	public JMSConsole(String url) {
		if (url != null && !url.isEmpty()) {
			logger.debug("Starting JMSFactory at " + url);
			connectionFactory.setBrokerURL(url);
		} else {
			logger.debug("Initializing JMSFactory at " + defaultURL);
			connectionFactory.setBrokerURL(defaultURL);
		}
	}

	public void addQueueListener(String queueName) {

	}

	public void removeQueueListener(String queueName)
	{
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	
	public static void main(String[] args){
		JMSConsole jmsConsole = new JMSConsole("");
		jmsConsole.run();
	}
}
