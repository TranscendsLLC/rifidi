package org.rifidi.edge.core.messageQueue.service;

import java.util.List;

import org.rifidi.edge.core.messageQueue.MessageQueue;

public interface MessageService {
	
	public MessageQueue createMessageQueue();

	public void destroyMessageQueue(MessageQueue messageQueue);
	
	public List<MessageQueue> getAllMessageQueues();
	
	public void addMessageQueueListener(MessageQueueListener listener);
	
	public void removeMessageQueueListener(MessageQueueListener listener);
}
