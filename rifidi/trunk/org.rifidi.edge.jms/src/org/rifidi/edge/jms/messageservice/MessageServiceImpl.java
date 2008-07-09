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
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class MessageServiceImpl implements MessageService {

	private ConnectionFactory connectionFactory;
	private ArrayList<MessageQueue> registry = new ArrayList<MessageQueue>();

	private Set<MessageServiceListener> listeners = new HashSet<MessageServiceListener>();

	public MessageServiceImpl(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
		ServiceRegistry.getInstance().service(this);
	}

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

	@Override
	public List<MessageQueue> getAllMessageQueues() {
		ArrayList<MessageQueue> retVal = new ArrayList<MessageQueue>(registry);
		return retVal;
	}

	@Override
	public void addMessageQueueListener(MessageServiceListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeMessageQueueListener(MessageServiceListener listener) {
		listeners.remove(listener);
	}

	private void fireAddEvent(MessageQueue event) {
		for (MessageServiceListener listener : listeners) {
			listener.addEvent(event);
		}
	}

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
