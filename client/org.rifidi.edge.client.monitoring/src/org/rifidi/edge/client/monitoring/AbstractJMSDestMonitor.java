/**
 * 
 */
package org.rifidi.edge.client.monitoring;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.print.attribute.standard.JobMediaSheetsCompleted;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.client.model.SALModelPlugin;
import org.springframework.jms.JmsException;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * This is an abstract class that provides the ability for subclasses to monitor
 * a JMS destination.
 * 
 * By default, it looks up the IP and port of the server using the
 * org.rifidi.edge.client.server.ip and org.rifidi.edge.client.server.port.jms
 * eclipse preferences.
 * 
 * TODO: Do we need to make this thread-safe?
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractJMSDestMonitor implements MessageListener {

	/** The Spring JMS Listener */
	private DefaultMessageListenerContainer container;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(AbstractJMSDestMonitor.class);
	private boolean stopped = true;

	/**
	 * Refreshes the JMS connection.
	 */
	public void refresh() {
		// a refresh is equivalent to a start.
		start();
	}

	/**
	 * Stops the JMS Connection.
	 */
	public void stop() {
		if (this.container != null) {
			this.stopped = true;
			this.container.stop();
			this.container.destroy();
			this.container = null;
		}
	}

	/**
	 * Starts the JMS Connection
	 */
	public void start() {
		// we have to stop it first.
		stop();
		String ip = getServerIP();
		String port = getServerPort();

		try {
			this.container = new DefaultMessageListenerContainer();
			this.container.setMessageListener(this);
			this.container.setDestination(new ActiveMQTopic(getDestination()));
			ActiveMQConnectionFactory connFac = new ActiveMQConnectionFactory();
			connFac.setBrokerURL("tcp://" + ip + ":" + port);
			//this.container.setAcceptMessagesWhileStopping(false);
			//this.container.setSubscriptionDurable(false);
			//this.container.setSessionTransacted(false);
			//this.container
			//		.setCacheLevel(DefaultMessageListenerContainer.CACHE_NONE);
			this.container.setConnectionFactory(connFac);
			this.container.afterPropertiesSet();
			this.container.start();
			this.stopped=false;
		} catch (JmsException ex) {
			stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message arg0) {
		if (arg0 instanceof TextMessage) {
			TextMessage tm = (TextMessage) arg0;
			try {
				if (!stopped)
					handleMessage(tm.getText());
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * This method should be overridden by subclasses in order to handle the
	 * incoming message.
	 * 
	 * @param message
	 *            - The incoming message as a string.
	 */
	protected void handleMessage(String message) {
		logger.debug("Received a message on " + getDestination() + " : "
				+ message);
	}

	/**
	 * Subclasses can override this method to return the IP address of the
	 * server. By default, returns the value stored in the eclipse preference at
	 * org.rifidi.edge.client.server.ip
	 * 
	 * @return The IP address of the server.
	 */
	protected String getServerIP() {
		return SALModelPlugin.getDefault().getPreferenceStore().getString(
				"org.rifidi.edge.client.server.ip");
	}

	/**
	 * Subclasses can override this method to return the port of the server. By
	 * default, returns the value stored in the eclipse preference at
	 * org.rifidi.edge.client.server.port
	 * 
	 * @return The port of the server.
	 */
	protected String getServerPort() {
		return SALModelPlugin.getDefault().getPreferenceStore().getString(
				"org.rifidi.edge.client.server.port.jms");
	}

	/**
	 * Subclasses should return this to return the JMS destination name.
	 * 
	 * @return The JMS destination name.
	 */
	protected abstract String getDestination();

}
