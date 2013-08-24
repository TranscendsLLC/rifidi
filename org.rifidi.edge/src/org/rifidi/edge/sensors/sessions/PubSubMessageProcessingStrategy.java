/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.sensors.sessions;

import java.util.Set;

import org.rifidi.edge.sensors.ByteMessage;

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
