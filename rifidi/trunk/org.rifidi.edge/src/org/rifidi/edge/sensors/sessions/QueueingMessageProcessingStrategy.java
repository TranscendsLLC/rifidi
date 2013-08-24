/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.sensors.sessions;

import java.util.Queue;

import org.rifidi.edge.sensors.ByteMessage;

/**
 * An implementation of MessageProcessingStrategy that puts incoming messages on a
 * queue
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramri.com
 */
public class QueueingMessageProcessingStrategy implements MessageProcessingStrategy {
	/** Reference to the message queue. */
	private Queue<ByteMessage> messageQueue;

	/**
	 * Constructor.
	 * 
	 * @param socket
	 * @param messageQueue
	 */
	public QueueingMessageProcessingStrategy(Queue<ByteMessage> messageQueue) {
		this.messageQueue = messageQueue;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.sensors.base.threads.MessageProcessingStrategy#processMessage(byte[])
	 */
	@Override
	public void processMessage(byte[] message) {
		ByteMessage mes = new ByteMessage(message);
		messageQueue.add(mes);
		
	}
	
	

}
