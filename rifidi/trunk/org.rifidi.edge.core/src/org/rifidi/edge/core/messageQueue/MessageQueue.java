package org.rifidi.edge.core.messageQueue;

import org.rifidi.edge.core.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.readerplugin.messages.Message;

public interface MessageQueue {
	
	public String getName();
	
	public void addMessage(Message message) throws RifidiMessageQueueException;
}
