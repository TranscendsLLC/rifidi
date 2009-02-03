package org.rifidi.edge.core.api.readerplugin.messageQueue;

import org.rifidi.edge.core.api.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.api.readerplugin.messages.Message;

/**
 * MessageQueue to store Messages on. This will be implemented in different
 * ways. It's mostly used to talk to the client or ALE.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface MessageQueue {

	/**
	 * Get the name of the MessagQueue
	 * 
	 * @return the name of the MessagQueue
	 */
	public String getName();

	/**
	 * Add a new Message to the Queue
	 * 
	 * @param message
	 *            message to add
	 * @throws RifidiMessageQueueException
	 *             if the message could not be added
	 */
	public void addMessage(Message message) throws RifidiMessageQueueException;
}
