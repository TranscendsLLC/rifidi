/*
 *  AlienIOStreamSensorSession.java
 *
 *  Created:	Mar 8, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.alien.iostream;

import java.util.Set;

import javax.jms.Destination;

import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.sessions.AbstractServerSocketSensorSession;
import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategyFactory;
import org.rifidi.edge.core.sensors.sessions.MessageProcessingStrategyFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * @author Matthew Dean
 *
 */
public class AlienIOStreamSensorSession extends
		AbstractServerSocketSensorSession {

	public AlienIOStreamSensorSession(AbstractSensor<?> sensor, String ID,
			Destination destination, JmsTemplate template,
			int serverSocketPort, int maxNumSensors,
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(sensor, ID, destination, template, serverSocketPort, maxNumSensors,
				commandConfigurations);
		
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.sessions.AbstractServerSocketSensorSession#getMessageParsingStrategyFactory()
	 */
	@Override
	protected MessageParsingStrategyFactory getMessageParsingStrategyFactory() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.sessions.AbstractServerSocketSensorSession#getMessageProcessingStrategyFactory()
	 */
	@Override
	protected MessageProcessingStrategyFactory getMessageProcessingStrategyFactory() {
		return null;
	}

}
