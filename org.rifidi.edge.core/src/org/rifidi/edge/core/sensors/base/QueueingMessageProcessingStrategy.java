package org.rifidi.edge.core.sensors.base;

import java.util.Queue;

import org.rifidi.edge.core.sensors.base.threads.MessageProcessingStrategy;
import org.rifidi.edge.core.sensors.messages.ByteMessage;

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
	 * @see org.rifidi.edge.core.sensors.base.threads.MessageProcessingStrategy#processMessage(byte[])
	 */
	@Override
	public void processMessage(byte[] message) {
		ByteMessage mes = new ByteMessage(message);
		messageQueue.add(mes);
		
	}
	
	

}
