/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.generic;

import java.util.Map;

import javax.management.MBeanInfo;

import org.rifidi.edge.exceptions.InvalidStateException;
import org.rifidi.edge.notification.NotifierService;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensorFactory;

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
	 * @see org.rifidi.edge.sensors.base.AbstractSensorFactory#bindCommandConfiguration(org.rifidi.edge.sensors.commands.AbstractCommandConfiguration, java.util.Map)
	 */
	@Override
	public void bindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.sensors.base.AbstractSensorFactory#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.sensors.base.AbstractSensorFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return displayname;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.sensors.base.AbstractSensorFactory#unbindCommandConfiguration(org.rifidi.edge.sensors.commands.AbstractCommandConfiguration, java.util.Map)
	 */
	@Override
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Map<?, ?> properties) {
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.configuration.ServiceFactory#createInstance(java.lang.String)
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
	 * @see org.rifidi.edge.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return FACTORY_ID;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.configuration.ServiceFactory#getServiceDescription(java.lang.String)
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
