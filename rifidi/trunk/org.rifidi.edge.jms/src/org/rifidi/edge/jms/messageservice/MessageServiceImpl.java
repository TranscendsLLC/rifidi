package org.rifidi.edge.jms.messageservice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.messageQueue.MessageQueue;
import org.rifidi.edge.core.messageQueue.service.MessageService;
import org.rifidi.edge.core.messageQueue.service.MessageServiceListener;
import org.rifidi.edge.jms.messagequeue.MessageQueueImpl;

/**
 * Implementation of the MessageQueueService. This allows to create and delete
 * MessageQueues. It also notifies subscribers about new created or deleted
 * MessageQueues.
 * 
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class MessageServiceImpl implements MessageService {

	private static final Log logger = LogFactory
			.getLog(MessageServiceImpl.class);

	private BrokerWrapper brokerWrapper;
	private Connection connection;
	private ArrayList<MessageQueue> registry = new ArrayList<MessageQueue>();
	private int numQueues = 0;

	private Set<MessageServiceListener> listeners = new HashSet<MessageServiceListener>();

	/**
	 * Create a new MessageServcice
	 * 
	 * @param connectionFactory
	 *            the ConnectionFactory to create new JMS Connections
	 */
	public MessageServiceImpl(BrokerWrapper brokerWrapper) {
		this.brokerWrapper = brokerWrapper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.messageQueue.service.MessageService#createMessageQueue
	 * (java.lang.String)
	 */
	@Override
	public synchronized MessageQueue createMessageQueue(String quename) {
		MessageQueueImpl messageQueue = new MessageQueueImpl(quename);
		try {
			if (numQueues == 0) {
				logger.debug("Creating new JMS Connection");
				connection = brokerWrapper.getConnectionFactory()
						.createConnection();
			}

			messageQueue.startMessageQueue(connection);

			// make sure to start connection after the session has already been
			// set up
			if (numQueues == 0) {
				connection.start();
			}
			numQueues++;
		} catch (JMSException e) {
			// e.printStackTrace();
			logger.error("Error creating MessageQueue ", e);
			return null;
		}
		registry.add(messageQueue);
		fireAddEvent(messageQueue);
		return messageQueue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.messageQueue.service.MessageService#destroyMessageQueue
	 * (org.rifidi.edge.core.messageQueue.MessageQueue)
	 */
	@Override
	public synchronized void destroyMessageQueue(MessageQueue messageQueue) {
		MessageQueueImpl mq = ((MessageQueueImpl) messageQueue);
		try {
			mq.stopMessageQueue();
		} catch (JMSException e) {
			logger.error(e);
		}

		try {
			logger.debug("Removing destination");
			mq.destroyDestination(brokerWrapper);
		} catch (Exception e) {
			logger.error(e);
		}

		numQueues--;
		if (numQueues == 0) {
			logger.debug("Closing JMS Connection");
			
			try {
				connection.stop();
			} catch (JMSException e) {
				logger.error(e);
			}
			
			try {
				connection.close();
			} catch (JMSException e) {
				logger.error(e);
			}

		}
		fireRemoveEvent(messageQueue);
		registry.remove(messageQueue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.messageQueue.service.MessageService#getAllMessageQueues
	 * ()
	 */
	@Override
	public List<MessageQueue> getAllMessageQueues() {
		ArrayList<MessageQueue> retVal = new ArrayList<MessageQueue>(registry);
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.messageQueue.service.MessageService#
	 * addMessageQueueListener
	 * (org.rifidi.edge.core.messageQueue.service.MessageServiceListener)
	 */
	@Override
	public void addMessageQueueListener(MessageServiceListener listener) {
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.messageQueue.service.MessageService#
	 * removeMessageQueueListener
	 * (org.rifidi.edge.core.messageQueue.service.MessageServiceListener)
	 */
	@Override
	public void removeMessageQueueListener(MessageServiceListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Fire add event. This event is fired if a MessageQueue was created.
	 * 
	 * @param event
	 *            the new MessageQueue created
	 */
	private void fireAddEvent(MessageQueue event) {
		for (MessageServiceListener listener : listeners) {
			listener.addEvent(event);
		}
	}

	/**
	 * Fire remove event. This event is fired if a MessageQueue was deleted.
	 * 
	 * @param event
	 *            the MessageQueue deleted
	 */
	private void fireRemoveEvent(MessageQueue event) {
		for (MessageServiceListener listener : listeners) {
			listener.removeEvent(event);
		}
	}

}
