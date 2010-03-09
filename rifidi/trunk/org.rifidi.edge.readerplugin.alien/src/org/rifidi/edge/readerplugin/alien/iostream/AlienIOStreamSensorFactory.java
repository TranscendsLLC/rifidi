/*
 *  AlienIOStreamSensorFactory.java
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

import org.rifidi.edge.core.exceptions.InvalidStateException;
import org.rifidi.edge.core.sensors.base.AbstractSensorFactory;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

/**
 * @author Matthew Dean
 *
 */
public class AlienIOStreamSensorFactory extends
		AbstractSensorFactory<AlienIOStreamSensor> {

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensorFactory#bindCommandConfiguration(org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration, java.util.Map)
	 */
	@Override
	public void bindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensorFactory#getDescription()
	 */
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensorFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensorFactory#unbindCommandConfiguration(org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration, java.util.Map)
	 */
	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.ServiceFactory#createInstance(java.lang.String)
	 */
	@Override
	public void createInstance(String serviceID)
			throws IllegalArgumentException, InvalidStateException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.ServiceFactory#getServiceDescription(java.lang.String)
	 */
	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		// TODO Auto-generated method stub
		return null;
	}

}
