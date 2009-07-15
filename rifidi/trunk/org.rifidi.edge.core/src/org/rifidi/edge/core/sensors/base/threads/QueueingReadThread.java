package org.rifidi.edge.core.sensors.base.threads;

import java.io.InputStream;
import java.util.Queue;

import org.rifidi.edge.core.sensors.messages.ByteMessage;

/**
 * An implementation of AbstractReadThread that puts incoming messages on a
 * queue
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class QueueingReadThread extends AbstractReadThread {
	/** Reference to the message queue. */
	private Queue<ByteMessage> messageQueue;

	/**
	 * Constructor.
	 * 
	 * @param socket
	 * @param messageQueue
	 */
	public QueueingReadThread(MessageParsingStrategy messageParser,
			InputStream inputStream, Queue<ByteMessage> messageQueue) {
		super(inputStream, messageParser);
		this.messageQueue = messageQueue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.threads.AbstractReadThread#processMessage
	 * (byte[])
	 */
	@Override
	protected void processMessage(byte[] message) {
		ByteMessage mes = new ByteMessage(message);
		messageQueue.add(mes);
	}

}
