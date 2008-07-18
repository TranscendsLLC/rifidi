package org.rifidi.edge.core.messagequeue.impl;

import java.util.concurrent.LinkedBlockingQueue;

import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.messages.Message;

/**
 * Implementation of a MessageQueue. This implementation will store all incoming
 * messages on a Queue and print them to the System console.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class MessageQueueImpl implements MessageQueue {
	// private static final Log logger =
	// LogFactory.getLog(MessageQueueImpl.class);

	private static int counter = 1;

	private String queueName;

	Thread thread;

	LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();

	/**
	 * Create a new MessageQueue
	 */
	public MessageQueueImpl() {
		queueName = Integer.toString(counter++);
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (true) {
						Message m;

						m = messageQueue.take();
						// logger.debug(m.toXML());
						System.out.println(m.toXML());
					}
				} catch (InterruptedException e) {
					// Ignore
				}

			}
		}, "MessageQueueTest: " + queueName);
		thread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.messageQueue.MessageQueue#addMessage(org.rifidi.edge.core.readerplugin.messages.Message)
	 */
	@Override
	public void addMessage(Message message) {
		System.out.println("New message added");
		messageQueue.add(message);
	}

	/**
	 * Stop the MessageQueue's internal thread
	 */
	public void stop() {
		thread.interrupt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.messageQueue.MessageQueue#getName()
	 */
	@Override
	public String getName() {
		return queueName;
	}

}
