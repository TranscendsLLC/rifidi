/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.sensors.sessions;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensor;

/**
 * An implementation of IPSensorSession with Publish-Subscribe semantics.
 * Clients can register themselves with this session and be notified when a new
 * message is available
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractPubSubIPSensorSession extends
		AbstractIPSensorSession {

	/** The list of subscribers */
	private final Set<IPSessionEndpoint> subscribers;
	/** A factory to produce new MessageProcessingStrategy objectsF */
	private final MessageProcessingStrategyFactory mpsf;

	/**
	 * @param sensor
	 * @param ID
	 * @param host
	 * @param port
	 * @param reconnectionInterval
	 * @param maxConAttempts
	 * @param commandConfigurations
	 */
	public AbstractPubSubIPSensorSession(AbstractSensor<?> sensor, String ID,
			String host, int port, int reconnectionInterval,
			int maxConAttempts,
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(sensor, ID, host, port, reconnectionInterval, maxConAttempts,
				commandConfigurations);
		subscribers = Collections
				.synchronizedSet(new HashSet<IPSessionEndpoint>());
		mpsf = new PubSubMessageProcessingStrategyFactory(subscribers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.base.AbstractIPSensorSession#
	 * clearUndelieverdMessages()
	 */
	@Override
	protected void clearUndelieverdMessages() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.base.AbstractIPSensorSession#
	 * getMessageProcessingStrategyFactory()
	 */
	@Override
	protected MessageProcessingStrategyFactory getMessageProcessingStrategyFactory() {
		return mpsf;
	}

	/**
	 * Subscribe to new messages.
	 * 
	 * @param subscriber
	 */
	public void subscribe(IPSessionEndpoint subscriber) {
		this.subscribers.add(subscriber);
	}

	/**
	 * Unsubscribe to messages
	 * 
	 * @param subscriber
	 */
	public void unsubscribe(IPSessionEndpoint subscriber) {
		this.subscribers.remove(subscriber);
	}

	/**
	 * A Factory that produces new PubSubMessageProcessingStrategy objects
	 * 
	 * @author Kyle Neumeier - kyle@pramari.com
	 * 
	 */
	private class PubSubMessageProcessingStrategyFactory implements
			MessageProcessingStrategyFactory {
		/** The subscribers */
		private final Set<IPSessionEndpoint> subscribers;

		/**
		 * @param subscribers
		 */
		public PubSubMessageProcessingStrategyFactory(
				Set<IPSessionEndpoint> subscribers) {
			this.subscribers = subscribers;
		}

		@Override
		public MessageProcessingStrategy createMessageProcessor() {
			return new PubSubMessageProcessingStrategy(subscribers);
		}
	}
}
