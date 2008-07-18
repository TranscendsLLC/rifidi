package org.rifidi.edge.jms.messageservice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.messageQueue.service.MessageService;
import org.rifidi.edge.core.messageQueue.service.MessageServiceListener;
import org.rifidi.edge.jms.messagequeue.MessageQueueImpl;
import org.rifidi.services.registry.ServiceRegistry;

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

	private ConnectionFactory connectionFactory;
	private ArrayList<MessageQueue> registry = new ArrayList<MessageQueue>();

	private Set<MessageServiceListener> listeners = new HashSet<MessageServiceListener>();

	/**
	 * Create a new MessageServcice
	 * 
	 * @param connectionFactory
	 *            the ConnectionFactory to create new JMS Connections
	 */
	public MessageServiceImpl(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
		ServiceRegistry.getInstance().service(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.messageQueue.service.MessageService#createMessageQueue(java.lang.String)
	 */
	@Override
	public MessageQueue createMessageQueue(String messageQueueName) {
		MessageQueueImpl messageQueue = new MessageQueueImpl(messageQueueName);
		try {
			messageQueue.startMessageQueue(connectionFactory);
		} catch (JMSException e) {
			e.printStackTrace();
			return null;
		}
		registry.add(messageQueue);
		fireAddEvent(messageQueue);
		return messageQueue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.messageQueue.service.MessageService#destroyMessageQueue(org.rifidi.edge.core.messageQueue.MessageQueue)
	 */
	@Override
	public void destroyMessageQueue(MessageQueue messageQueue) {
		try {
			((MessageQueueImpl) messageQueue).stopMessageQueue();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		fireRemoveEvent(messageQueue);
		registry.remove(messageQueue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.messageQueue.service.MessageService#getAllMessageQueues()
	 */
	@Override
	public List<MessageQueue> getAllMessageQueues() {
		ArrayList<MessageQueue> retVal = new ArrayList<MessageQueue>(registry);
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.messageQueue.service.MessageService#addMessageQueueListener(org.rifidi.edge.core.messageQueue.service.MessageServiceListener)
	 */
	@Override
	public void addMessageQueueListener(MessageServiceListener listener) {
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.messageQueue.service.MessageService#removeMessageQueueListener(org.rifidi.edge.core.messageQueue.service.MessageServiceListener)
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

	// Not needed because passed in at the constructor
	// @Inject
	// public void setConnectionFactory(ConnectionFactory connectionFactory) {
	// this.connectionFactory = connectionFactory;
	// }

}
