package org.rifidi.edge.core.messageQueue.service;

import java.util.List;

import org.rifidi.edge.core.messageQueue.MessageQueue;

public interface MessageService {
	
	public MessageQueue createMessageQueue(String messageQueueName);

	public void destroyMessageQueue(MessageQueue messageQueue);
	
	public List<MessageQueue> getAllMessageQueues();
	
	public void addMessageQueueListener(MessageServiceListener listener);
	
	public void removeMessageQueueListener(MessageServiceListener listener);
}
