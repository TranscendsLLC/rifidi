/*
 *  AlienIOStreamSensor.java
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

import java.util.Map;

import javax.management.MBeanInfo;

import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.exceptions.CannotCreateSessionException;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.exceptions.CannotDestroySensorException;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class AlienIOStreamSensor extends
		AbstractSensor<AlienIOStreamSensorSession> {

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#applyPropertyChanges()
	 */
	@Override
	public void applyPropertyChanges() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#createSensorSession()
	 */
	@Override
	public String createSensorSession() throws CannotCreateSessionException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#createSensorSession(org.rifidi.edge.api.rmi.dto.SessionDTO)
	 */
	@Override
	public String createSensorSession(SessionDTO sessionDTO)
			throws CannotCreateSessionException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#destroySensorSession(java.lang.String)
	 */
	@Override
	public void destroySensorSession(String id)
			throws CannotDestroySensorException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#getDisplayName()
	 */
	@Override
	protected String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#getSensorSessions()
	 */
	@Override
	public Map<String, SensorSession> getSensorSessions() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensor#unbindCommandConfiguration(org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration, java.util.Map)
	 */
	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.services.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
