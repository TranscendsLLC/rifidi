package org.rifidi.edge.core.messagequeue.impl;

import java.util.concurrent.LinkedBlockingQueue;

import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.messages.Message;

public class MessageQueueImpl implements MessageQueue {

	private static int counter = 1;
	
	private String queueName;
	
	Thread thread;

	LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();

	public MessageQueueImpl() {
		queueName =Integer.toString(counter++);
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (true) {
						Message m;

						m = messageQueue.take();

						System.out.println(m.toXML());
					}
				} catch (InterruptedException e) {
					// Ignore
				}

			}
		}, "MessageQueueTest");
		thread.start();
	}

	@Override
	public void addMessage(Message message) {
		System.out.println("New message added");
		messageQueue.add(message);
	}

	public void stop() {
		thread.interrupt();
	}

	@Override
	public String getName() {
		return queueName;
	}

}
