package org.rifidi.edge.core.messageQueue.service;

import org.rifidi.edge.core.api.readerplugin.messageQueue.EventQueue;

/**
 * Listener to monitor the MessageService for new MessageQueues and for the
 * deletion of a MessageQueue.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface MessageServiceListener {
	/**
	 * New MessageQueue created event
	 * 
	 * @param messageQueue
	 *            the new MessageQueue
	 */
	public void addEvent(EventQueue messageQueue);

	/**
	 * MessageQueue removed event
	 * 
	 * @param messageQueue
	 *            the messageQueue removed
	 */
	public void removeEvent(EventQueue messageQueue);
}
