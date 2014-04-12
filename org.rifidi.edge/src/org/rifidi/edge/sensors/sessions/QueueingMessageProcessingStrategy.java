/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
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
