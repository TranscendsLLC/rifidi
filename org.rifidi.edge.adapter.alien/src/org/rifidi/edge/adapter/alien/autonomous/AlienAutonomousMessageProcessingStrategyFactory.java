/*
 * 
 * AlienAutonomousMessageProcessingStrategyFactory.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
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
