package org.rifidi.edge.core.messagequeue.service;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.messageQueue.service.MessageService;
import org.rifidi.edge.core.messageQueue.service.MessageServiceListener;
import org.rifidi.edge.core.messagequeue.impl.MessageQueueImpl;

/**
 * Implementation of a MessageService. It allows to create and destroy
 * MessageQueues. It also allows to monitor these events.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class MessageServiceImpl implements MessageService {

	private List<MessageServiceListener> listeners = new ArrayList<MessageServiceListener>();

	private ArrayList<MessageQueue> messageQueues = new ArrayList<MessageQueue>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.messageQueue.service.MessageService#createMessageQueue(java.lang.String)
	 */
	@Override
	public MessageQueue createMessageQueue(String messageQueueName) {
		MessageQueue messageQueue = new MessageQueueImpl();
		messageQueues.add(messageQueue);
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
		messageQueues.remove(messageQueue);
		((MessageQueueImpl) messageQueue).stop();
		fireRemoveEvent(messageQueue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.messageQueue.service.MessageService#getAllMessageQueues()
	 */
	@Override
	public List<MessageQueue> getAllMessageQueues() {
		return new ArrayList<MessageQueue>(messageQueues);
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
	 * Fire message queue created event
	 * 
	 * @param event
	 *            MessageQueue created
	 */
	private void fireAddEvent(MessageQueue event) {
		for (MessageServiceListener listener : listeners) {
			listener.addEvent(event);
		}
	}

	/**
	 * Fire message queue removed event
	 * 
	 * @param event
	 *            MessageQueue removed
	 */
	private void fireRemoveEvent(MessageQueue event) {
		for (MessageServiceListener listener : listeners) {
			listener.removeEvent(event);
		}
	}

}
