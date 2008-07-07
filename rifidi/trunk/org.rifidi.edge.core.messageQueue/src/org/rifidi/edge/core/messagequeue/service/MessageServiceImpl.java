package org.rifidi.edge.core.messagequeue.service;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.messageQueue.service.MessageQueueListener;
import org.rifidi.edge.core.messageQueue.service.MessageService;
import org.rifidi.edge.core.messagequeue.impl.MessageQueueImpl;

public class MessageServiceImpl implements MessageService {

	private List<MessageQueueListener> listeners = new ArrayList<MessageQueueListener>();

	private ArrayList<MessageQueue> messageQueues = new ArrayList<MessageQueue>();

	@Override
	public MessageQueue createMessageQueue() {
		MessageQueue messageQueue = new MessageQueueImpl();
		messageQueues.add(messageQueue);
		fireAddEvent(messageQueue);
		return messageQueue;
	}

	@Override
	public void destroyMessageQueue(MessageQueue messageQueue) {
		messageQueues.remove(messageQueue);
		((MessageQueueImpl)messageQueue).stop();
		fireRemoveEvent(messageQueue);
	}

	@Override
	public List<MessageQueue> getAllMessageQueues() {
		return new ArrayList<MessageQueue>(messageQueues);
	}

	@Override
	public void addMessageQueueListener(MessageQueueListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeMessageQueueListener(MessageQueueListener listener) {
		listeners.remove(listener);
	}

	private void fireAddEvent(MessageQueue event) {
		for (MessageQueueListener listener : listeners) {
			listener.addEvent(event);
		}
	}

	private void fireRemoveEvent(MessageQueue event) {
		for (MessageQueueListener listener : listeners) {
			listener.removeEvent(event);
		}
	}

}
