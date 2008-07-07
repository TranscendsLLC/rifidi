package org.rifidi.edge.core.messageQueue;

import org.rifidi.edge.core.readerplugin.messages.Message;

public interface MessageQueue {
	public void addMessage(Message message);
}
