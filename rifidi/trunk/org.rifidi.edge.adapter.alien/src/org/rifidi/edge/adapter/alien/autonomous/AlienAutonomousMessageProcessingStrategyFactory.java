/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
/**
 * 
 */
package org.rifidi.edge.adapter.alien.autonomous;

import org.rifidi.edge.sensors.sessions.MessageProcessingStrategy;
import org.rifidi.edge.sensors.sessions.MessageProcessingStrategyFactory;

/**
 * A factory for producing new AlienAutonomousMessageProcessingStrategy
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienAutonomousMessageProcessingStrategyFactory implements
		MessageProcessingStrategyFactory {

	/** The session */
	private AlienAutonomousSensorSession session;

	/**
	 * Constructor
	 * 
	 * @param session
	 * @param template
	 */
	public AlienAutonomousMessageProcessingStrategyFactory(
			AlienAutonomousSensorSession session) {
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.threads.MessageProcessingStrategyFactory
	 * #createMessageProcessor()
	 */
	@Override
	public MessageProcessingStrategy createMessageProcessor() {
		return new AlienAutonomousMessageProcessingStrategy(session);
	}

}
