/*
 *  AcuraProXReaderSession.java
 *
 *  Created:	Dec 3, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.acura;

import java.io.IOException;
import java.util.Set;

import javax.jms.Destination;

import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.commands.Command;
import org.rifidi.edge.core.sensors.sessions.AbstractSensorSession;
import org.springframework.jms.core.JmsTemplate;

/**
 * @author Matthew Dean
 *
 */
public class AcuraProXReaderSession extends AbstractSensorSession {

	public AcuraProXReaderSession(AbstractSensor<?> sensor, String ID,
			Destination destination, JmsTemplate template,
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(sensor, ID, destination, template, commandConfigurations);
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.SensorSession#_connect()
	 */
	@Override
	protected void _connect() throws IOException {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.SensorSession#disconnect()
	 */
	@Override
	public void disconnect() {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.SensorSession#getResetCommand()
	 */
	@Override
	protected Command getResetCommand() {
		
		return null;
	}

}
