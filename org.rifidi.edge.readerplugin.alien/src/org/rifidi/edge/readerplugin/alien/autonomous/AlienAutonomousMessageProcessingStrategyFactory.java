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
package org.rifidi.edge.readerplugin.alien.autonomous;

import org.rifidi.edge.core.sensors.sessions.MessageProcessingStrategy;
import org.rifidi.edge.core.sensors.sessions.MessageProcessingStrategyFactory;
import org.springframework.jms.core.JmsTemplate;

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
	/** Template used to send notifications */
	private JmsTemplate template;

	/**
	 * Constructor
	 * 
	 * @param session
	 * @param template
	 */
	public AlienAutonomousMessageProcessingStrategyFactory(
			AlienAutonomousSensorSession session, JmsTemplate template) {
		this.session = session;
		this.template = template;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.threads.MessageProcessingStrategyFactory
	 * #createMessageProcessor()
	 */
	@Override
	public MessageProcessingStrategy createMessageProcessor() {
		return new AlienAutonomousMessageProcessingStrategy(session, template);
	}

}
