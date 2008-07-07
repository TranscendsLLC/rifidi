package org.rifidi.edge.core.messageQueue.service;

import org.rifidi.edge.core.messageQueue.MessageQueue;

public interface MessageQueueListener {
	public void addEvent(MessageQueue messageQueue);
	public void removeEvent(MessageQueue messageQueue);
}
