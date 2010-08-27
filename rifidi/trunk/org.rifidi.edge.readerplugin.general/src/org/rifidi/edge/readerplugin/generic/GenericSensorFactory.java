/*
 *  GenericSensorFactory.java
 *
 *  Created:	Aug 5, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.generic;

import java.util.Map;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.exceptions.InvalidStateException;
import org.rifidi.edge.core.sensors.base.AbstractSensorFactory;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.services.notification.NotifierService;

/**
 * Factory class for the Generic reader.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class GenericSensorFactory extends AbstractSensorFactory<GenericSensor> {

	

	/** The Unique FACTORY_ID for this Factory */
	public static final String FACTORY_ID = "Generic";
	/** Description of the sensorSession. */
	private static final String description = "A generic Rifidi Adapter.  ";
	/** The name of the reader that will be displayed */
	private static final String displayname = "Generic";
	/** A JMS event notification sender */
	private volatile NotifierService notifierService;
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensorFactory#bindCommandConfiguration(org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration, java.util.Map)
	 */
	@Override
	public void bindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensorFactory#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensorFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return displayname;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.AbstractSensorFactory#unbindCommandConfiguration(org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration, java.util.Map)
	 */
	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.ServiceFactory#createInstance(java.lang.String)
	 */
	@Override
	public void createInstance(String serviceID)
			throws IllegalArgumentException, InvalidStateException {
		if (serviceID == null) {
			throw new IllegalArgumentException("ServiceID is null");
		}
		if (notifierService == null) {
			throw new InvalidStateException("All services are not set");
		}
		GenericSensor instance = new GenericSensor();
		instance.setID(serviceID);
		instance.setNotifiyService(notifierService);
		instance.register(getContext(), FACTORY_ID);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return FACTORY_ID;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.configuration.ServiceFactory#getServiceDescription(java.lang.String)
	 */
	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo)GenericSensor.mbeaninfo;
	}
	
	/**
	 * Called by spring
	 * 
	 * @param wrapper
	 */
	public void setNotifierService(NotifierService notifierService) {
		this.notifierService = notifierService;
	}


}
