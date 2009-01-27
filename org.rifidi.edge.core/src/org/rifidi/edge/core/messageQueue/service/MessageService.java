package org.rifidi.edge.core.messageQueue.service;

import java.util.List;

import org.rifidi.edge.core.api.messageQueue.MessageQueue;

/**
 * MessageService for creating and deleting MessageQueues. This will be
 * implemented in different ways. It's the minimal set of functions a
 * MessageService needs to provide. It also has a list of all created
 * MessageQueues.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface MessageService {

	/**
	 * Create a new MessageQueue
	 * 
	 * @param messageQueueName
	 *            name of the MessageQueue to create
	 * @return the MessageQueue
	 */
	public MessageQueue createMessageQueue(String messageQueueName);

	/**
	 * Delete a previously created MessageQueue
	 * 
	 * @param messageQueue
	 *            MessageQueue to delete
	 */
	public void destroyMessageQueue(MessageQueue messageQueue);

	/**
	 * Get a list of all MessageQueues in this service
	 * 
	 * @return a list of MessageQueues
	 */
	public List<MessageQueue> getAllMessageQueues();

	/**
	 * Add a listener. Listening for the creation and deletion of MessageQueues.
	 * 
	 * @param listener
	 *            Listener for for the creation and deletion of MessageQueues
	 */
	public void addMessageQueueListener(MessageServiceListener listener);

	/**
	 * Remove a previously added listener
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeMessageQueueListener(MessageServiceListener listener);
}
