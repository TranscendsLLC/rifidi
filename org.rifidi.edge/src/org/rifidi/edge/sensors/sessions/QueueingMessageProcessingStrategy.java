/*
 * 
 * QueueingMessageProcessingStrategy.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
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
