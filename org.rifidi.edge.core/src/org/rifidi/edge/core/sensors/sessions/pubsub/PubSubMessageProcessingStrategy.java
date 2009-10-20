/*
 * 
 * PubSubMessageProcessingStrategy.java
 *  
 * Created:     Oct 16th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.sensors.sessions.pubsub;

import java.util.Set;

import org.rifidi.edge.core.sensors.messages.ByteMessage;
import org.rifidi.edge.core.sensors.sessions.MessageProcessingStrategy;

/**
 * This is a MessageProcessingStrategy that will notify subscribers when a new
 * message has arrived
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class PubSubMessageProcessingStrategy implements
		MessageProcessingStrategy {

	/** The set of subscribers to notify when there is a new message */
	private final Set<IPSessionEndpoint> subscribers;

	/**
	 * @param subscribers
	 *            The set of subscribers to notify when there is a new message
	 */
	public PubSubMessageProcessingStrategy(
			Set<IPSessionEndpoint> subscribers) {
		this.subscribers = subscribers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.sessions.MessageProcessingStrategy#
	 * processMessage(byte[])
	 */
	@Override
	public void processMessage(byte[] message) {
		ByteMessage byteMessage = new ByteMessage(message);
		synchronized (subscribers) {
			for (IPSessionEndpoint subscriber : subscribers) {
				subscriber.handleMessage(byteMessage);
			}
		}
	}

}
