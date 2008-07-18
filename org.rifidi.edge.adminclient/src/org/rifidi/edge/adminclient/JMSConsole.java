package org.rifidi.edge.adminclient;

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

public class JMSConsole implements Runnable {
	private Log logger = LogFactory.getLog(JMSConsole.class);
	
	@SuppressWarnings("unused")
	private final String connectionURL = "tcp://localhost:61616";
	
	private static final String defaultURL = "tcp://localhost:61616";
	private static final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();

	private Connection connection;
	private Session session;
	
	private boolean running = true;
	
	private List<MessageConsumer> consumers = new ArrayList<MessageConsumer>();
	
	public JMSConsole(String url) {
		if (url != null && !url.isEmpty()) {
			logger.debug("Starting JMSFactory at " + url);
			connectionFactory.setBrokerURL(url);
		} else {
			logger.debug("Initializing JMSFactory at " + defaultURL);
			connectionFactory.setBrokerURL(defaultURL);
		}
		try {
			connection = connectionFactory.createConnection();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		try {
			connection.start();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void addQueueListener(String queueName) throws JMSException {
		Destination dest = session.createQueue(queueName);
		MessageConsumer consumer = session.createConsumer(dest);
		consumers.add(consumer);
		
	}

	public void removeQueueListener(String queueName)
	{
		try {
			session.unsubscribe(queueName);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while (running){
			for (MessageConsumer consumer: consumers){
				try {
					System.out.println(consumer.receive());
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
			
		}
	}

	
	public static void main(String[] args){
		JMSConsole jmsConsole = new JMSConsole("");
		jmsConsole.run();
		
		
	}
	
	public void finalize(){
		try {
			session.close();
			connection.stop();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}
}
