package org.rifidi.edge.adminclient;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.rifidi.edge.adminclient.jms.JMSConsumerFactory;

public class JMS implements Runnable {

	public static void main(String[] args) {
		try {
			new JMS();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private MessageConsumer consumer;

	public JMS() throws Exception {
		connectionFactory = new ActiveMQConnectionFactory();
		connectionFactory.setBrokerURL("tcp://192.168.1.115:61616");
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination dest = session.createQueue("1");
		consumer = session.createConsumer(dest);

		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		try {
			while (true) {
				TextMessage message;

				message = (TextMessage) consumer.receive();
				System.out.println(message.getText());

			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
